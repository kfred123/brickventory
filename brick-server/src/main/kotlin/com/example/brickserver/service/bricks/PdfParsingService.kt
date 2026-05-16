package com.example.brickserver.service.bricks

import com.example.brickserver.domain.bricks.Brick
import com.example.brickserver.domain.bricks.BrickImage
import com.example.brickserver.domain.bricks.BrickSet
import com.example.brickserver.domain.bricks.BrickSetPart
import com.example.brickserver.repository.bricks.BrickImageRepository
import com.example.brickserver.repository.bricks.BrickRepository
import com.example.brickserver.repository.bricks.BrickSetPartRepository
import com.example.brickserver.repository.bricks.BrickSetRepository
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

data class BrickPart(val quantity: Int, val partNumber: String, val pageNumber: Int)

data class PdfProcessingResult(
        val partsDetected: Int,
        val bricksCreated: Int,
        val imagesSaved: Int
)

data class SetProcessingResult(
        val setId: UUID?,
        val setCreated: Boolean,
        val partsDetected: Int,
        val bricksCreated: Int,
        val partsLinked: Int,
        val imagesSaved: Int
)

@Service
class PdfParsingService(
        @Value("\${app.upload.dir:uploads}") private val uploadDir: String,
        private val brickRepository: BrickRepository,
        private val brickImageRepository: BrickImageRepository,
        private val brickSetRepository: BrickSetRepository,
        private val brickSetPartRepository: BrickSetPartRepository
) {

    private val logger = LoggerFactory.getLogger(PdfParsingService::class.java)

    init {
        Files.createDirectories(Paths.get(uploadDir))
    }

    fun parsePdfForParts(file: MultipartFile): List<BrickPart> {
        val parts = mutableListOf<BrickPart>()

        try {
            file.inputStream.use { inputStream ->
                val document = Loader.loadPDF(inputStream.readBytes())

                try {
                    val pdfStripper = PDFTextStripper()
                    val totalPages = document.numberOfPages

                    // Process each page separately to track which page contains which parts
                    for (pageNum in 1..totalPages) {
                        pdfStripper.startPage = pageNum
                        pdfStripper.endPage = pageNum
                        val pageText = pdfStripper.getText(document)

                        // Pattern to match: number followed by 'x' followed by part number
                        val pattern = Regex("""(\d+)\s*x\s*(\S+)""", RegexOption.IGNORE_CASE)

                        pattern.findAll(pageText).forEach { matchResult ->
                            val quantity = matchResult.groupValues[1].toIntOrNull() ?: 0
                            val partNumber = matchResult.groupValues[2].trim()

                            if (quantity > 0 && partNumber.isNotEmpty()) {
                                val part =
                                        BrickPart(
                                                quantity,
                                                partNumber,
                                                pageNum - 1
                                        ) // 0-indexed for PDFRenderer
                                parts.add(part)
                                logger.info(
                                        "Detected brick part: ${quantity}x $partNumber on page $pageNum"
                                )
                            }
                        }
                    }

                    logger.info("Total parts detected: ${parts.size}")
                } finally {
                    document.close()
                }
            }
        } catch (e: Exception) {
            logger.error("Error parsing PDF: ${e.message}", e)
            throw RuntimeException("Failed to parse PDF file: ${e.message}", e)
        }

        return parts
    }

    @Transactional
    fun processPdfWithImages(file: MultipartFile): PdfProcessingResult {
        var bricksCreated = 0
        var imagesSaved = 0

        try {
            file.inputStream.use { inputStream ->
                val document = Loader.loadPDF(inputStream.readBytes())

                try {
                    val totalPages = document.numberOfPages

                    // Process each page
                    for (pageNum in 1..totalPages) {
                        val page = document.getPage(pageNum - 1)

                        // 1. Extract Text with Coordinates
                        val stripper = LocationTextStripper()
                        stripper.startPage = pageNum
                        stripper.endPage = pageNum
                        stripper.getText(document)
                        val textLocations = stripper.textLocations

                        // 2. Extract Images with Coordinates
                        val imageExtractor = PdfImageExtractor()
                        imageExtractor.processPage(page)
                        val extractedImages = imageExtractor.extractedImages

                        if (extractedImages.isEmpty()) {
                            logger.warn("No images found on page $pageNum")
                            continue
                        }

                        // 3. Find Part Numbers and Match to Images
                        val pattern = Regex("""(\d+)\s*x\s*(\S+)""", RegexOption.IGNORE_CASE)
                        val fullText = stripper.text
                        val matches = pattern.findAll(fullText)

                        // Track which images and text locations have already been assigned
                        // so each part gets its own unique closest image
                        val usedImages = mutableSetOf<ExtractedImage>()
                        val usedTextLocations = mutableSetOf<TextLocation>()
                        val pageHeight = page.mediaBox.height

                        for (match in matches) {
                            val quantity = match.groupValues[1].toIntOrNull() ?: 0
                            val partNumber = match.groupValues[2].trim()

                            if (quantity > 0 && partNumber.isNotEmpty()) {
                                // Find the TextLocation for this part number,
                                // skipping locations already used by a previous part
                                val partLocation =
                                        textLocations.find {
                                            it.text.contains(partNumber) && it !in usedTextLocations
                                        }

                                if (partLocation != null) {
                                    usedTextLocations.add(partLocation)

                                    // 4. Find Nearest *unused* Image
                                    val availableImages =
                                            extractedImages.filter { it !in usedImages }
                                    val nearestImage =
                                            availableImages.minByOrNull { image ->
                                                val textCenterX = partLocation.x
                                                val textCenterY = partLocation.y

                                                // Convert Image Y from bottom-up (PDF) to
                                                // top-down (PDFTextStripper)
                                                val imageYTopDown = pageHeight - image.y
                                                val imgCenterX = image.x + (image.width / 2)
                                                val imgCenterY = imageYTopDown - (image.height / 2)

                                                // Euclidean distance squared
                                                val dx = textCenterX - imgCenterX
                                                val dy = textCenterY - imgCenterY
                                                dx * dx + dy * dy
                                            }

                                    if (nearestImage != null) {
                                        usedImages.add(nearestImage)

                                        // Check or create Brick
                                        val brick =
                                                brickRepository.findByPartNum(partNumber)
                                                        ?: run {
                                                            logger.info(
                                                                    "Brick $partNumber does not exist, creating new entry"
                                                            )
                                                            val newBrick =
                                                                    Brick(
                                                                            partNum = partNumber,
                                                                            name = partNumber
                                                                    )
                                                            bricksCreated++
                                                            brickRepository.save(newBrick)
                                                        }

                                        // Save Image
                                        try {
                                            val filename = "${partNumber}_${System.nanoTime()}.png"
                                            val imagePath = Paths.get(uploadDir).resolve(filename)

                                            ImageIO.write(
                                                    nearestImage.image,
                                                    "png",
                                                    imagePath.toFile()
                                            )
                                            logger.info(
                                                    "Saved extracted image for part $partNumber: $filename"
                                            )

                                            val brickImage =
                                                    BrickImage(
                                                            brick = brick,
                                                            imagePath = imagePath.toString(),
                                                            isVerified = false
                                                    )
                                            brickImageRepository.save(brickImage)
                                            imagesSaved++
                                        } catch (e: Exception) {
                                            logger.warn(
                                                    "Failed to save image for part $partNumber: ${e.message}"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    logger.info(
                            "Processing complete: $bricksCreated bricks created, $imagesSaved images saved"
                    )

                    return PdfProcessingResult(
                            partsDetected =
                                    0, // We are not tracking total parts in this specific flow
                            // anymore, or we can add a counter
                            bricksCreated = bricksCreated,
                            imagesSaved = imagesSaved
                    )
                } finally {
                    document.close()
                }
            }
        } catch (e: Exception) {
            logger.error("Error processing PDF with images: ${e.message}", e)
            throw RuntimeException("Failed to process PDF file: ${e.message}", e)
        }
    }

    @Transactional
    fun processSetPdf(
            source: String,
            setNum: String,
            file: MultipartFile
    ): SetProcessingResult {
        var bricksCreated = 0
        var imagesSaved = 0
        var partsLinked = 0

        try {
            file.inputStream.use { inputStream ->
                val document = Loader.loadPDF(inputStream.readBytes())

                try {
                    // 1. Parse PDF for parts
                    val parts = parsePdfWithDocument(document)
                    logger.info("Parsed ${parts.size} parts from PDF for set $source/$setNum")

                    // 2. Find or create the BrickSet
                    var setCreated = false
                    val brickSet =
                            brickSetRepository.findBySourceAndSetNum(source, setNum)
                                    ?: run {
                                        setCreated = true
                                        logger.info("Creating new BrickSet: $source/$setNum")
                                        brickSetRepository.save(
                                                BrickSet(
                                                        source = source,
                                                        setNum = setNum
                                                )
                                        )
                                    }

                    // 3. If set already existed, clear old parts and update name
                    if (!setCreated) {
                        logger.info("Set $source/$setNum already exists, clearing old parts")
                        brickSetPartRepository.deleteBySet(brickSet)
                    }

                    // 4. For each part, find or create Brick + create BrickSetPart entry
                    for (part in parts) {
                        val brick =
                                brickRepository.findByPartNum(part.partNumber)
                                        ?: run {
                                            bricksCreated++
                                            brickRepository.save(
                                                    Brick(
                                                            partNum = part.partNumber,
                                                            name = part.partNumber
                                                    )
                                            )
                                        }

                        brickSetPartRepository.save(
                                BrickSetPart(
                                        set = brickSet,
                                        brick = brick,
                                        quantity = part.quantity
                                )
                        )
                        partsLinked++
                    }

                    // 5. Update numParts on the set
                    brickSet.numParts = parts.sumOf { it.quantity }
                    brickSetRepository.save(brickSet)

                    // 6. Extract and save images
                    val totalPages = document.numberOfPages
                    for (pageNum in 1..totalPages) {
                        val page = document.getPage(pageNum - 1)
                        val imageExtractor = PdfImageExtractor()
                        imageExtractor.processPage(page)
                        val extractedImages = imageExtractor.extractedImages

                        if (extractedImages.isEmpty()) continue

                        val stripper = LocationTextStripper()
                        stripper.startPage = pageNum
                        stripper.endPage = pageNum
                        stripper.getText(document)
                        val textLocations = stripper.textLocations

                        val pattern = Regex("""(\d+)\s*x\s*(\S+)""", RegexOption.IGNORE_CASE)
                        val fullText = stripper.text
                        val matches = pattern.findAll(fullText)

                        val usedImages = mutableSetOf<ExtractedImage>()
                        val usedTextLocations = mutableSetOf<TextLocation>()
                        val pageHeight = page.mediaBox.height

                        for (match in matches) {
                            val partNumber = match.groupValues[2].trim()
                            if (partNumber.isEmpty()) continue

                            val partLocation =
                                    textLocations.find {
                                        it.text.contains(partNumber) && it !in usedTextLocations
                                    }
                                            ?: continue
                            usedTextLocations.add(partLocation)

                            val availableImages = extractedImages.filter { it !in usedImages }
                            val nearestImage =
                                    availableImages.minByOrNull { image ->
                                        val imageYTopDown = pageHeight - image.y
                                        val imgCenterX = image.x + (image.width / 2)
                                        val imgCenterY = imageYTopDown - (image.height / 2)
                                        val dx = partLocation.x - imgCenterX
                                        val dy = partLocation.y - imgCenterY
                                        dx * dx + dy * dy
                                    }
                                            ?: continue
                            usedImages.add(nearestImage)

                            val brick = brickRepository.findByPartNum(partNumber) ?: continue

                            try {
                                val filename = "${partNumber}_${System.nanoTime()}.png"
                                val imagePath = Paths.get(uploadDir).resolve(filename)
                                ImageIO.write(nearestImage.image, "png", imagePath.toFile())
                                brickImageRepository.save(
                                        BrickImage(
                                                brick = brick,
                                                imagePath = imagePath.toString(),
                                                isVerified = false
                                        )
                                )
                                imagesSaved++
                            } catch (e: Exception) {
                                logger.warn(
                                        "Failed to save image for part $partNumber: ${e.message}"
                                )
                            }
                        }
                    }

                    logger.info(
                            "Set processing complete for $source/$setNum: " +
                                    "$bricksCreated bricks created, $partsLinked parts linked, " +
                                    "$imagesSaved images saved"
                    )

                    return SetProcessingResult(
                            setId = brickSet.id,
                            setCreated = setCreated,
                            partsDetected = parts.size,
                            bricksCreated = bricksCreated,
                            partsLinked = partsLinked,
                            imagesSaved = imagesSaved
                    )
                } finally {
                    document.close()
                }
            }
        } catch (e: Exception) {
            logger.error("Error processing set PDF for $source/$setNum: ${e.message}", e)
            throw RuntimeException("Failed to process set PDF: ${e.message}", e)
        }
    }

    private fun parsePdfWithDocument(document: PDDocument): List<BrickPart> {
        val parts = mutableListOf<BrickPart>()
        val pdfStripper = PDFTextStripper()
        val totalPages = document.numberOfPages

        for (pageNum in 1..totalPages) {
            pdfStripper.startPage = pageNum
            pdfStripper.endPage = pageNum
            val pageText = pdfStripper.getText(document)

            val pattern = Regex("""(\d+)\s*x\s*(\S+)""", RegexOption.IGNORE_CASE)

            pattern.findAll(pageText).forEach { matchResult ->
                val quantity = matchResult.groupValues[1].toIntOrNull() ?: 0
                val partNumber = matchResult.groupValues[2].trim()

                if (quantity > 0 && partNumber.isNotEmpty()) {
                    parts.add(BrickPart(quantity, partNumber, pageNum - 1))
                    // logger.info("Detected brick part: ${quantity}x $partNumber on page $pageNum")
                }
            }
        }

        return parts
    }
}

data class TextLocation(val text: String, val x: Float, val y: Float)

class LocationTextStripper : PDFTextStripper() {
    val textLocations = mutableListOf<TextLocation>()

    // Simple text property to capture full string
    val text: String
        get() = output.toString()

    init {
        // This ensures the internal `output` buffer is populated so we can do .toString()
    }

    override fun writeString(
            text: String,
            textPositions: List<TextPosition>
    ) {
        if (text.isNotBlank()) {
            val validPositions = textPositions.filter { !it.unicode.isNullOrBlank() }
            if (validPositions.isNotEmpty()) {
                val first = validPositions.first()
                // Capture the X/Y of the start of the word/string
                // TextPosition.getY() is usually top-down in PDFTextStripper
                textLocations.add(TextLocation(text, first.xDirAdj, first.yDirAdj))
            }
        }
        super.writeString(text, textPositions)
    }
}

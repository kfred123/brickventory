package com.example.brickserver.controller.bricks

import com.example.brickserver.domain.bricks.BrickSource
import com.example.brickserver.service.bricks.PdfParsingService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/maintenance")
class MaintenanceController(private val pdfParsingService: PdfParsingService) {

    private val logger = LoggerFactory.getLogger(MaintenanceController::class.java)

    @PostMapping(
            "/sets/source/{source}/set-number/{setNum}/upload-parts-pdf",
            consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadSetPdf(
            @PathVariable source: String,
            @PathVariable setNum: String,
            @RequestPart("file") file: MultipartFile
    ): ResponseEntity<Map<String, Any>> {
        val brickSource =
                BrickSource.fromPath(source)
                        ?: return ResponseEntity.badRequest()
                                .body(
                                        mapOf(
                                                "error" to
                                                        "Invalid source '$source'. Allowed values: ${BrickSource.entries.joinToString { it.name.lowercase() }}"
                                        )
                                )

        if (file.contentType != "application/pdf") {
            return ResponseEntity.badRequest().body(mapOf("error" to "Only PDF files are allowed"))
        }

        try {
            logger.info(
                    "Processing PDF for set ${brickSource.displayName}/$setNum): ${file.originalFilename}"
            )

            val result =
                    pdfParsingService.processSetPdf(
                            brickSource.displayName,
                            setNum,
                            file
                    )

            logger.info(
                    "Successfully processed PDF for set $source/$setNum. " +
                            "Set created: ${result.setCreated}, parts: ${result.partsDetected}, " +
                            "bricks created: ${result.bricksCreated}, " +
                            "parts linked: ${result.partsLinked}, " +
                            "images: ${result.imagesSaved}"
            )

            return ResponseEntity.ok(
                    mapOf(
                            "message" to "PDF processed for set $source/$setNum",
                            "filename" to (file.originalFilename ?: "unknown"),
                            "setId" to (result.setId?.toString() ?: ""),
                            "setCreated" to result.setCreated,
                            "partsDetected" to result.partsDetected,
                            "bricksCreated" to result.bricksCreated,
                            "partsLinked" to result.partsLinked,
                            "imagesSaved" to result.imagesSaved
                    )
            )
        } catch (e: Exception) {
            logger.error("Error processing PDF for set $source/$setNum: ${e.message}", e)
            return ResponseEntity.internalServerError()
                    .body(mapOf("error" to "Failed to process PDF: ${e.message}"))
        }
    }
}

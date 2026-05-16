package com.example.brickserver.service.bricks

import com.example.brickserver.domain.bricks.BrickImage
import com.example.brickserver.repository.bricks.BrickImageRepository
import com.example.brickserver.repository.bricks.BrickRepository
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService(
        @Value("\${app.upload.dir:uploads}") private val uploadDir: String,
        private val brickImageRepository: BrickImageRepository,
        private val brickRepository: BrickRepository
) {

    init {
        Files.createDirectories(Paths.get(uploadDir))
    }

    fun uploadImage(partNum: String, file: MultipartFile): BrickImage {
        val brick =
                brickRepository.findByPartNum(partNum)
                        ?: throw IllegalArgumentException("Brick not found")

        val filename = "${UUID.randomUUID()}_${file.originalFilename}"
        val targetPath = Paths.get(uploadDir).resolve(filename)
        Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)

        val image = BrickImage(brick = brick, imagePath = targetPath.toString())
        return brickImageRepository.save(image)
    }
}

package com.example.brickserver.controller.bricks

import com.example.brickserver.controller.bricks.dto.BrickDetailResponse
import com.example.brickserver.repository.bricks.BrickImageRepository
import com.example.brickserver.repository.bricks.BrickRepository
import java.io.File
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bricks")
class BrickController(
        private val brickRepository: BrickRepository,
        private val brickImageRepository: BrickImageRepository
) {
    private val logger = LoggerFactory.getLogger(BrickController::class.java)

    @GetMapping
    fun getBricks(@RequestParam(required = false) search: String?): List<BrickDetailResponse> {
        val bricks =
                if (search != null) {
                    brickRepository.findByNameContainingIgnoreCase(search)
                } else {
                    brickRepository.findAll().take(100)
                }
        return bricks.map { BrickDetailResponse.from(it) }
    }

    @GetMapping("/{brickId}")
    fun getBrick(@PathVariable brickId: UUID): ResponseEntity<BrickDetailResponse> {
        val brick =
                brickRepository.findById(brickId).orElse(null)
                        ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(BrickDetailResponse.from(brick))
    }

    @GetMapping("/images/{imageId}")
    fun getBrickImage(@PathVariable imageId: UUID): ResponseEntity<Resource> {
        logger.info("getBrickImage called with imageId: $imageId")
        val brickImage =
                brickImageRepository.findById(imageId).orElse(null)
                        ?: return ResponseEntity.notFound().build()

        val file = File(brickImage.imagePath)

        if (!file.exists()) {
            return ResponseEntity.notFound().build()
        }

        val resource = FileSystemResource(file)

        var contentType: MediaType = MediaType.IMAGE_PNG
        val fileName = file.name.lowercase()
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = MediaType.IMAGE_JPEG
        }

        return ResponseEntity.ok().contentType(contentType).body(resource)
    }
}

package com.example.brickserver.controller.bricks

import com.example.brickserver.domain.bricks.BrickImage
import com.example.brickserver.service.bricks.ImageService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
class ImageController(private val imageService: ImageService) {

    @PostMapping("/upload")
    fun uploadImage(
        @RequestParam("partNum") partNum: String,
        @RequestParam("file") file: MultipartFile
    ): BrickImage {
        return imageService.uploadImage(partNum, file)
    }
}

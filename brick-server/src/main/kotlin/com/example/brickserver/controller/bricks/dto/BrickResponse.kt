package com.example.brickserver.controller.bricks.dto

import com.example.brickserver.domain.bricks.Brick
import com.example.brickserver.domain.bricks.BrickImage
import java.util.UUID
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

data class BrickImageResponse(val id: UUID, val url: String, val isVerified: Boolean) {
    companion object {
        fun from(image: BrickImage): BrickImageResponse {
            val baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
            return BrickImageResponse(
                    id = image.id!!,
                    url = "$baseUrl/api/bricks/images/${image.id}",
                    isVerified = image.isVerified
            )
        }
    }
}

data class BrickDetailResponse(
        val id: UUID,
        val partNum: String,
        val name: String,
        val images: List<BrickImageResponse>
) {
    companion object {
        fun from(brick: Brick): BrickDetailResponse {
            return BrickDetailResponse(
                    id = brick.id!!,
                    partNum = brick.partNum,
                    name = brick.name,
                    images = brick.images.map { BrickImageResponse.from(it) }
            )
        }
    }
}

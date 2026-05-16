package com.example.brickserver.controller.bricks.dto

import java.util.UUID

data class BrickSetDetailResponse(
        val id: UUID,
        val source: String,
        val setNum: String,
        val name: String,
        val description: String?,
        val year: Int?,
        val numParts: Int?,
        val setImgUrl: String?,
        val parts: List<BrickSetPartResponse>
)

data class BrickSetPartResponse(
        val partId: UUID,
        val partNum: String,
        val name: String,
        val quantity: Int
)

data class UpdateBrickSetRequest(
        val name: String? = null,
        val description: String? = null
)

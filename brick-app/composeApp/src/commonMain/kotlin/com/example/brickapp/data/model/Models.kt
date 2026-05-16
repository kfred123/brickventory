
package com.example.brickapp.data.model

import kotlinx.serialization.Serializable

// ---- Auth ----

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val email: String, val password: String, val displayName: String)

@Serializable
data class AuthResponse(val token: String, val userId: String, val displayName: String)

// ---- Bricks ----

@Serializable
data class BrickImageResponse(val id: String, val url: String, val isVerified: Boolean)

@Serializable
data class BrickDetailResponse(
    val id: String,
    val partNum: String,
    val name: String,
    val images: List<BrickImageResponse> = emptyList()
)

// ---- Sets ----

@Serializable
data class BrickSetResponse(
    val id: String,
    val source: String,
    val setNum: String,
    val name: String,
    val year: Int? = null,
    val themeId: Int? = null,
    val numParts: Int? = null,
    val setImgUrl: String? = null
)

@Serializable
data class BrickSetDetailResponse(
    val id: String,
    val source: String,
    val setNum: String,
    val name: String,
    val year: Int? = null,
    val numParts: Int? = null,
    val setImgUrl: String? = null,
    val parts: List<BrickSetPartResponse> = emptyList()
)

@Serializable
data class BrickSetPartResponse(
    val partId: String,
    val partNum: String,
    val name: String,
    val quantity: Int
)

// ---- User Collection ----

@Serializable
data class AddBrickRequest(val brickId: String, val quantity: Int = 1, val notes: String? = null)

@Serializable
data class AddSetRequest(val setId: String, val quantity: Int = 1, val notes: String? = null)

@Serializable
data class UpdateCollectionRequest(val quantity: Int, val notes: String? = null)

@Serializable
data class UserBrickResponse(
    val id: String,
    val brickId: String,
    val partNum: String,
    val brickName: String,
    val quantity: Int,
    val notes: String? = null
)

@Serializable
data class UserSetResponse(
    val id: String,
    val setId: String,
    val setNum: String,
    val setName: String,
    val quantity: Int,
    val notes: String? = null
)

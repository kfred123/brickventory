package com.example.brickserver.controller.user.dto

import java.util.UUID

data class RegisterRequest(val email: String, val password: String, val displayName: String)

data class LoginRequest(val email: String, val password: String)

data class AuthResponse(val token: String, val userId: UUID, val displayName: String)

package com.example.brickserver.controller.user

import com.example.brickserver.controller.user.dto.AuthResponse
import com.example.brickserver.controller.user.dto.LoginRequest
import com.example.brickserver.controller.user.dto.RegisterRequest
import com.example.brickserver.service.user.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        return try {
            val result = authService.register(request.email, request.password, request.displayName)
            ResponseEntity.ok(
                AuthResponse(
                    token = result.token,
                    userId = result.userId,
                    displayName = result.displayName
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return try {
            val result = authService.login(request.email, request.password)
            ResponseEntity.ok(
                AuthResponse(
                    token = result.token,
                    userId = result.userId,
                    displayName = result.displayName
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(401).build()
        }
    }
}
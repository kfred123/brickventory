package com.example.brickserver.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HealthController {

    @GetMapping("/health")
    fun health(): ResponseEntity<HealthCheckResponse> {
        return ResponseEntity.ok(
            HealthCheckResponse(
                status = "UP",
                timestamp = System.currentTimeMillis(),
                version = "1.0.0"
            )
        )
    }
}

data class HealthCheckResponse(
    val status: String,
    val timestamp: Long,
    val version: String
)

package com.example.brickserver.controller

import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HealthController(private val jdbcTemplate: JdbcTemplate?) {

    @GetMapping("/health")
    fun health(): ResponseEntity<HealthCheckResponse> {
        val timestamp = System.currentTimeMillis()
        var dbStatus = "UNKNOWN"
        try {
            if (jdbcTemplate != null) {
                val result: Int? = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
                dbStatus = if (result == 1) "UP" else "DEGRADED"
            } else {
                dbStatus = "NO_DB"
            }
        } catch (e: Exception) {
            dbStatus = "DOWN: ${e.message}"
        }

        val overall = if (dbStatus.startsWith("UP") || dbStatus == "NO_DB") "UP" else "DEGRADED"

        return ResponseEntity.ok(
            HealthCheckResponse(
                status = overall,
                timestamp = timestamp,
                version = "1.0.0",
                db = dbStatus
            )
        )
    }
}

data class HealthCheckResponse(
    val status: String,
    val timestamp: Long,
    val version: String,
    val db: String
)

package com.example.brickserver.service.user

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class JwtService(
        @Value("\${app.jwt.secret}") private val secret: String,
        @Value("\${app.jwt.expiration-ms}") private val expirationMs: Long
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))
    }

    fun generateToken(userId: UUID, email: String): String {
        val now = Date()
        val expiry = Date(now.time + expirationMs)

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact()
    }

    fun validateTokenAndGetUserId(token: String): UUID? {
        return try {
            val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
            UUID.fromString(claims.subject)
        } catch (e: Exception) {
            null
        }
    }
}

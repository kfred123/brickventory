package com.example.brickserver.service.user

import com.example.brickserver.domain.user.User
import com.example.brickserver.repository.user.UserRepository
import java.util.UUID
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

data class AuthResponse(val token: String, val userId: UUID, val displayName: String)

@Service
class AuthService(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val jwtService: JwtService
) {
    fun register(email: String, password: String, displayName: String): AuthResponse {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already registered")
        }

        val user =
                userRepository.save(
                        User(
                                email = email,
                                passwordHash = passwordEncoder.encode(password),
                                displayName = displayName
                        )
                )

        val token = jwtService.generateToken(user.id!!, user.email)
        return AuthResponse(token, user.id, user.displayName)
    }

    fun login(email: String, password: String): AuthResponse {
        val user =
                userRepository.findByEmail(email)
                        ?: throw IllegalArgumentException("Invalid email or password")

        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        val token = jwtService.generateToken(user.id!!, user.email)
        return AuthResponse(token, user.id, user.displayName)
    }
}

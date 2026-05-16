package com.example.brickserver.domain.user

import com.example.brickserver.domain.BaseEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
        @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
        @Column(unique = true, nullable = false) val email: String,
        @Column(name = "password_hash", nullable = false) val passwordHash: String,
        @Column(name = "display_name", nullable = false) val displayName: String
) : BaseEntity()

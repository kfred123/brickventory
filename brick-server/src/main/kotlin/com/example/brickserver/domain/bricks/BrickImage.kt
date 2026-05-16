package com.example.brickserver.domain.bricks

import com.example.brickserver.domain.BaseEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "brick_images")
data class BrickImage(
        @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "brick_id", nullable = false) // Join by UUID FK
        val brick: Brick,
        @Column(name = "image_path", nullable = false) val imagePath: String,
        @Column(name = "is_verified")
        val isVerified: Boolean = false // User uploaded vs verified training data
) : BaseEntity()

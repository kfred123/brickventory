package com.example.brickserver.domain.user

import com.example.brickserver.domain.BaseEntity
import com.example.brickserver.domain.bricks.Brick
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
        name = "user_bricks",
        uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "brick_id"])]
)
data class UserBrick(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "brick_id", nullable = false)
        val brick: Brick,
    @Column(nullable = false) var quantity: Int = 1,
    var notes: String? = null
) : BaseEntity()

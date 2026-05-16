package com.example.brickserver.domain.bricks

import com.example.brickserver.domain.BaseEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "brick_set_parts")
data class BrickSetPart(
        @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "set_id") val set: BrickSet? = null,
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "brick_id") val brick: Brick? = null,
        @Column(nullable = false) val quantity: Int = 1
) : BaseEntity()

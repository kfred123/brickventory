package com.example.brickserver.domain.user

import com.example.brickserver.domain.BaseEntity
import com.example.brickserver.domain.bricks.BrickSet
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
        name = "user_sets",
        uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "set_id"])]
)
data class UserSet(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "set_id", nullable = false)
        val set: BrickSet,
    @Column(nullable = false) var quantity: Int = 1,
    var notes: String? = null
) : BaseEntity()

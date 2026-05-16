package com.example.brickserver.domain.bricks

import com.example.brickserver.domain.BaseEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "bricks")
data class Brick(
        @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
        @Column(name = "part_num", nullable = false, unique = true)
        val partNum: String, // e.g. "3001"
        @Column(nullable = false) val name: String,
        @OneToMany(
                mappedBy = "brick",
                cascade = [CascadeType.ALL],
                orphanRemoval = true,
                fetch = FetchType.LAZY
        )
        @JsonIgnore
        val images: List<BrickImage> = emptyList()
) : BaseEntity()

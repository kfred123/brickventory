package com.example.brickserver.domain.bricks

import com.example.brickserver.domain.BaseEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
        name = "brick_sets",
        uniqueConstraints = [UniqueConstraint(columnNames = ["source", "set_num"])]
)
data class BrickSet(
        @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
        @Column(nullable = false) val source: String, // e.g. "Lego", "BlueBrixx"
        @Column(name = "set_num", nullable = false) val setNum: String, // e.g. "75192-1"
        @Column(nullable = false) var name: String = "",
        @Column(name = "year_released") var year: Int? = null,
        @Column(name = "theme_id") var themeId: Int? = null,
        @Column(name = "num_parts") var numParts: Int? = null,
        @Column(name = "set_img_url") var setImgUrl: String? = null,
        @Column var description: String? = null
) : BaseEntity()

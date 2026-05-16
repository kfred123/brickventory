package com.example.brickserver.repository.bricks

import com.example.brickserver.domain.bricks.Brick
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BrickRepository : JpaRepository<Brick, UUID> {
    fun findByNameContainingIgnoreCase(name: String): List<Brick>
    fun findByPartNum(partNum: String): Brick?
}

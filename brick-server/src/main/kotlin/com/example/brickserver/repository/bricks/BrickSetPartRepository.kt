package com.example.brickserver.repository.bricks

import com.example.brickserver.domain.bricks.BrickSet
import com.example.brickserver.domain.bricks.BrickSetPart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BrickSetPartRepository : JpaRepository<BrickSetPart, UUID> {
    fun findBySet(set: BrickSet): List<BrickSetPart>
    fun deleteBySet(set: BrickSet)
}

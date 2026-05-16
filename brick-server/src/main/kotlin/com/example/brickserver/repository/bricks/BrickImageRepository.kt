package com.example.brickserver.repository.bricks

import com.example.brickserver.domain.bricks.BrickImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BrickImageRepository : JpaRepository<BrickImage, UUID> {
    fun findByBrickPartNum(partNum: String): List<BrickImage>
    fun findByBrickId(brickId: UUID): List<BrickImage>
}

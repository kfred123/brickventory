package com.example.brickserver.repository.bricks

import com.example.brickserver.domain.bricks.BrickSet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BrickSetRepository : JpaRepository<BrickSet, UUID> {
    fun findByYear(year: Int): List<BrickSet>
    fun findBySetNum(setNum: String): BrickSet?
    fun findBySourceAndSetNum(source: String, setNum: String): BrickSet?
}

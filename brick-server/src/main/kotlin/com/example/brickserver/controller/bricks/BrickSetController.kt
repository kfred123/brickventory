package com.example.brickserver.controller.bricks

import com.example.brickserver.controller.bricks.dto.BrickSetDetailResponse
import com.example.brickserver.controller.bricks.dto.BrickSetPartResponse
import com.example.brickserver.controller.bricks.dto.UpdateBrickSetRequest
import com.example.brickserver.domain.bricks.BrickSet
import com.example.brickserver.repository.bricks.BrickSetPartRepository
import com.example.brickserver.repository.bricks.BrickSetRepository
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sets")
class BrickSetController(
        private val brickSetRepository: BrickSetRepository,
        private val brickSetPartRepository: BrickSetPartRepository
) {
    @GetMapping("")
    fun getSets(@RequestParam(required = false) year: Int?): List<BrickSet> {
        return if (year != null) {
            brickSetRepository.findByYear(year)
        } else {
            brickSetRepository.findAll().take(100)
        }
    }

    @GetMapping("/{setId}")
    fun getSetById(@PathVariable setId: UUID): ResponseEntity<BrickSetDetailResponse> {
        val brickSet =
                brickSetRepository.findById(setId).orElse(null)
                        ?: return ResponseEntity.notFound().build()

        val parts =
                brickSetPartRepository.findBySet(brickSet).map { part ->
                    BrickSetPartResponse(
                            partId = part.brick?.id!!,
                            partNum = part.brick?.partNum ?: "",
                            name = part.brick?.name ?: "",
                            quantity = part.quantity
                    )
                }

        return ResponseEntity.ok(
                BrickSetDetailResponse(
                        id = brickSet.id!!,
                        source = brickSet.source,
                        setNum = brickSet.setNum,
                        name = brickSet.name,
                        description = brickSet.description,
                        year = brickSet.year,
                        numParts = brickSet.numParts,
                        setImgUrl = brickSet.setImgUrl,
                        parts = parts
                )
        )
    }

    @PutMapping("/{setId}")
    fun updateSet(
            @PathVariable setId: UUID,
            @RequestBody request: UpdateBrickSetRequest
    ): ResponseEntity<BrickSet> {
        val brickSet =
                brickSetRepository.findById(setId).orElse(null)
                        ?: return ResponseEntity.notFound().build()

        request.name?.let { brickSet.name = it }
        request.description?.let { brickSet.description = it }

        val updated = brickSetRepository.save(brickSet)
        return ResponseEntity.ok(updated)
    }
}

package com.example.brickserver.controller.user

import com.example.brickserver.controller.user.dto.AddBrickRequest
import com.example.brickserver.controller.user.dto.AddSetRequest
import com.example.brickserver.controller.user.dto.UpdateCollectionRequest
import com.example.brickserver.controller.user.dto.UserBrickResponse
import com.example.brickserver.controller.user.dto.UserSetResponse
import com.example.brickserver.domain.user.UserBrick
import com.example.brickserver.domain.user.UserSet
import com.example.brickserver.repository.bricks.BrickRepository
import com.example.brickserver.repository.bricks.BrickSetRepository
import com.example.brickserver.repository.user.UserBrickRepository
import com.example.brickserver.repository.user.UserRepository
import com.example.brickserver.repository.user.UserSetRepository
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/")
class UserCollectionController(
        private val userRepository: UserRepository,
        private val brickRepository: BrickRepository,
        private val brickSetRepository: BrickSetRepository,
        private val userBrickRepository: UserBrickRepository,
        private val userSetRepository: UserSetRepository
) {

        private fun currentUserId(): UUID {
                       return SecurityContextHolder.getContext().authentication.principal as UUID
        }

        // ---- Bricks ----

        @GetMapping("/bricks")
        fun getMyBricks(): List<UserBrickResponse> {
                return userBrickRepository.findByUserId(currentUserId()).map {
                        UserBrickResponse.Companion.from(it)
                }
        }

        @PostMapping("/bricks")
        fun addBrick(@RequestBody request: AddBrickRequest): ResponseEntity<UserBrickResponse> {
                val userId = currentUserId()
                val user =
                        userRepository.findById(userId).orElse(null)
                                ?: return ResponseEntity.notFound().build()
                val brick =
                        brickRepository.findById(request.brickId).orElse(null)
                                ?: return ResponseEntity.notFound().build()

                // Check if already in collection
                val existing = userBrickRepository.findByUserIdAndBrickId(userId, request.brickId)
                if (existing != null) {
                        existing.quantity += request.quantity
                        if (request.notes != null) existing.notes = request.notes
                        val saved = userBrickRepository.save(existing)
                        return ResponseEntity.ok(UserBrickResponse.Companion.from(saved))
                }

                val userBrick =
                        userBrickRepository.save(
                                UserBrick(
                                        user = user,
                                        brick = brick,
                                        quantity = request.quantity,
                                        notes = request.notes
                                )
                        )
                return ResponseEntity.ok(UserBrickResponse.Companion.from(userBrick))
        }

        @PutMapping("/bricks/{brickId}")
        fun updateBrick(
                @PathVariable brickId: UUID,
                @RequestBody request: UpdateCollectionRequest
        ): ResponseEntity<UserBrickResponse> {
                val userId = currentUserId()
                val existing =
                        userBrickRepository.findByUserIdAndBrickId(userId, brickId)
                                ?: return ResponseEntity.notFound().build()

                existing.quantity = request.quantity
                existing.notes = request.notes
                val saved = userBrickRepository.save(existing)
                return ResponseEntity.ok(UserBrickResponse.Companion.from(saved))
        }

        @DeleteMapping("/bricks/{brickId}")
        @Transactional
        fun removeBrick(@PathVariable brickId: UUID): ResponseEntity<Void> {
                val userId = currentUserId()
                userBrickRepository.deleteByUserIdAndBrickId(userId, brickId)
                return ResponseEntity.noContent().build()
        }

        // ---- Sets ----

        @GetMapping("/sets")
        fun getMySets(): List<UserSetResponse> {
                return userSetRepository.findByUserId(currentUserId()).map {
                        UserSetResponse.Companion.from(it)
                }
        }

        @PostMapping("/sets")
        fun addSet(@RequestBody request: AddSetRequest): ResponseEntity<UserSetResponse> {
                val userId = currentUserId()
                val user =
                        userRepository.findById(userId).orElse(null)
                                ?: return ResponseEntity.notFound().build()
                val brickSet =
                        brickSetRepository.findById(request.setId).orElse(null)
                                ?: return ResponseEntity.notFound().build()

                val existing = userSetRepository.findByUserIdAndSetId(userId, request.setId)
                if (existing != null) {
                        existing.quantity += request.quantity
                        if (request.notes != null) existing.notes = request.notes
                        val saved = userSetRepository.save(existing)
                        return ResponseEntity.ok(UserSetResponse.Companion.from(saved))
                }

                val userSet =
                        userSetRepository.save(
                                UserSet(
                                        user = user,
                                        set = brickSet,
                                        quantity = request.quantity,
                                        notes = request.notes
                                )
                        )
                return ResponseEntity.ok(UserSetResponse.Companion.from(userSet))
        }

        @PutMapping("/sets/{setId}")
        fun updateSet(
                @PathVariable setId: UUID,
                @RequestBody request: UpdateCollectionRequest
        ): ResponseEntity<UserSetResponse> {
                val userId = currentUserId()
                val existing =
                        userSetRepository.findByUserIdAndSetId(userId, setId)
                                ?: return ResponseEntity.notFound().build()

                existing.quantity = request.quantity
                existing.notes = request.notes
                val saved = userSetRepository.save(existing)
                return ResponseEntity.ok(UserSetResponse.Companion.from(saved))
        }

        @DeleteMapping("/sets/{setId}")
        @Transactional
        fun removeSet(@PathVariable setId: UUID): ResponseEntity<Void> {
                val userId = currentUserId()
                userSetRepository.deleteByUserIdAndSetId(userId, setId)
                return ResponseEntity.noContent().build()
        }
}

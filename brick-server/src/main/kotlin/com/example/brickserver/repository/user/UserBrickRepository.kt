package com.example.brickserver.repository.user

import com.example.brickserver.domain.user.UserBrick
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserBrickRepository : JpaRepository<UserBrick, UUID> {
    fun findByUserId(userId: UUID): List<UserBrick>
    fun findByUserIdAndBrickId(userId: UUID, brickId: UUID): UserBrick?
    fun deleteByUserIdAndBrickId(userId: UUID, brickId: UUID)
}

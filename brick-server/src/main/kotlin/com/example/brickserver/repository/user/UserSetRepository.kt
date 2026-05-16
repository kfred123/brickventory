package com.example.brickserver.repository.user

import com.example.brickserver.domain.user.UserSet
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserSetRepository : JpaRepository<UserSet, UUID> {
    fun findByUserId(userId: UUID): List<UserSet>
    fun findByUserIdAndSetId(userId: UUID, setId: UUID): UserSet?
    fun deleteByUserIdAndSetId(userId: UUID, setId: UUID)
}

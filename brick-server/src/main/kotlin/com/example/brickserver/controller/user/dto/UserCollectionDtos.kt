package com.example.brickserver.controller.user.dto

import com.example.brickserver.domain.user.UserBrick
import com.example.brickserver.domain.user.UserSet
import java.util.UUID

data class AddBrickRequest(val brickId: UUID, val quantity: Int = 1, val notes: String? = null)

data class AddSetRequest(val setId: UUID, val quantity: Int = 1, val notes: String? = null)

data class UpdateCollectionRequest(val quantity: Int, val notes: String? = null)

data class UserBrickResponse(
        val id: UUID,
        val brickId: UUID,
        val partNum: String,
        val brickName: String,
        val quantity: Int,
        val notes: String?
) {
    companion object {
        fun from(ub: UserBrick): UserBrickResponse {
            return UserBrickResponse(
                    id = ub.id!!,
                    brickId = ub.brick.id!!,
                    partNum = ub.brick.partNum,
                    brickName = ub.brick.name,
                    quantity = ub.quantity,
                    notes = ub.notes
            )
        }
    }
}

data class UserSetResponse(
        val id: UUID,
        val setId: UUID,
        val setNum: String,
        val setName: String,
        val quantity: Int,
        val notes: String?
) {
    companion object {
        fun from(us: UserSet): UserSetResponse {
            return UserSetResponse(
                    id = us.id!!,
                    setId = us.set.id!!,
                    setNum = us.set.setNum,
                    setName = us.set.name,
                    quantity = us.quantity,
                    notes = us.notes
            )
        }
    }
}

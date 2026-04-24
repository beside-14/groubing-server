package com.beside.groubing.groubingserver.domain.friend.domain

import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException

data class Friend private constructor(
    val id: Long,
    val inviterId: Long,
    val inviteeId: Long,
    val status: FriendStatus
) {
    fun isInvitee(memberId: Long): Boolean = inviteeId == memberId

    fun accept(memberId: Long): Friend {
        validateStatusTransitionBy(memberId)
        return copy(status = FriendStatus.ACCEPT)
    }

    fun reject(memberId: Long): Friend {
        validateStatusTransitionBy(memberId)
        return copy(status = FriendStatus.REJECT)
    }

    fun repend(): Friend {
        check(status.isReject()) { "거절된 친구 요청만 다시 요청할 수 있습니다." }
        return copy(status = FriendStatus.PENDING)
    }

    private fun validateStatusTransitionBy(memberId: Long) {
        if (!isInvitee(memberId)) {
            throw FriendInputException("당사자가 아니면 친구 요청을 수락하거나 거절할 수 없습니다.")
        }
        if (!status.isPending()) {
            throw FriendInputException("이미 등록된 친구이거나 친구 요청 상태가 아닙니다. status : $status")
        }
    }

    companion object {
        fun create(inviterId: Long, inviteeId: Long): Friend {
            if (inviterId == inviteeId) {
                throw FriendInputException("나 자신을 친구 요청할 수 없습니다.")
            }
            return Friend(
                id = 0L,
                inviterId = inviterId,
                inviteeId = inviteeId,
                status = FriendStatus.PENDING
            )
        }

        fun of(id: Long, inviterId: Long, inviteeId: Long, status: FriendStatus): Friend {
            return Friend(id = id, inviterId = inviterId, inviteeId = inviteeId, status = status)
        }
    }
}

package com.beside.groubing.domain.blockedmember.domain

import com.beside.groubing.domain.blockedmember.exception.BlockedMemberInputException

class BlockedMember private constructor(
    val id: Long,
    val requesterId: Long,
    val targetMemberId: Long
) {
    fun isBlockedMember(memberId: Long): Boolean = requesterId == memberId

    fun validateUnblockAuthority(requesterId: Long) {
        if (!isBlockedMember(requesterId)) {
            throw BlockedMemberInputException("차단을 해제할 권한이 없습니다.")
        }
    }

    companion object {
        fun create(requesterId: Long, targetMemberId: Long): BlockedMember {
            return BlockedMember(id = 0L, requesterId = requesterId, targetMemberId = targetMemberId)
        }

        fun of(id: Long, requesterId: Long, targetMemberId: Long): BlockedMember {
            return BlockedMember(id = id, requesterId = requesterId, targetMemberId = targetMemberId)
        }
    }
}

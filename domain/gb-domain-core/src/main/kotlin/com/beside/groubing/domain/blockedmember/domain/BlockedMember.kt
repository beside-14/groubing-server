package com.beside.groubing.domain.blockedmember.domain

class BlockedMember private constructor(
    val id: Long,
    val requesterId: Long,
    val targetMemberId: Long
) {
    fun isBlockedMember(memberId: Long): Boolean = requesterId == memberId

    companion object {
        fun create(requesterId: Long, targetMemberId: Long): BlockedMember {
            return BlockedMember(id = 0L, requesterId = requesterId, targetMemberId = targetMemberId)
        }

        fun of(id: Long, requesterId: Long, targetMemberId: Long): BlockedMember {
            return BlockedMember(id = id, requesterId = requesterId, targetMemberId = targetMemberId)
        }
    }
}

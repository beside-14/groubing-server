package com.beside.groubing.groubingserver.domain.blockedmember.payload.response

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberTargetInfo

data class BlockedMemberResponse(
    val id: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?
) {
    companion object {
        fun create(info: BlockedMemberTargetInfo): BlockedMemberResponse {
            return BlockedMemberResponse(
                id = info.id,
                email = info.email,
                nickname = info.nickname,
                profileUrl = info.profileFileName?.let { "/api/files/$it" }
            )
        }
    }
}

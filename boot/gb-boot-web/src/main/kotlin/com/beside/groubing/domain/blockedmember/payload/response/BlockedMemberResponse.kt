package com.beside.groubing.domain.blockedmember.payload.response

import com.beside.groubing.domain.blockedmember.domain.BlockedMemberTarget
import com.beside.groubing.global.domain.file.domain.FileInfo

data class BlockedMemberResponse(
    val id: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?
) {
    companion object {
        fun of(target: BlockedMemberTarget): BlockedMemberResponse {
            return BlockedMemberResponse(
                id = target.id,
                email = target.email,
                nickname = target.nickname,
                profileUrl = FileInfo.urlOfOrNull(target.profileFileName)
            )
        }
    }
}

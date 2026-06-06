package com.beside.groubing.domain.blockedmember.payload.response

import com.beside.groubing.domain.blockedmember.domain.BlockedMemberTarget
import com.beside.groubing.domain.common.file.domain.FileInfo
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

data class BlockedMemberResponse(
    @EncryptId(ObfuscationType.MEMBER)
    val memberId: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?
) {
    companion object {
        fun of(target: BlockedMemberTarget): BlockedMemberResponse {
            return BlockedMemberResponse(
                memberId = target.id,
                email = target.email,
                nickname = target.nickname,
                profileUrl = FileInfo.urlOfOrNull(target.profileFileName)
            )
        }
    }
}

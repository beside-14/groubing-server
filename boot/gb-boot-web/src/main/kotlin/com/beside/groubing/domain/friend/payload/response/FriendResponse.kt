package com.beside.groubing.domain.friend.payload.response

import com.beside.groubing.domain.friend.domain.FriendMember
import com.beside.groubing.global.domain.file.domain.FileInfo
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

data class FriendResponse(
    val id: Long,
    @EncryptId(ObfuscationType.MEMBER)
    val memberId: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?
) {
    companion object {
        fun of(friendMember: FriendMember): FriendResponse = FriendResponse(
            id = friendMember.friendId,
            memberId = friendMember.memberId,
            email = friendMember.email,
            nickname = friendMember.nickname,
            profileUrl = FileInfo.urlOfOrNull(friendMember.profileFileName)
        )
    }
}

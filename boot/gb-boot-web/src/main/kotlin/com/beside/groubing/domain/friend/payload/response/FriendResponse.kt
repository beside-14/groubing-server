package com.beside.groubing.domain.friend.payload.response

import com.beside.groubing.domain.friend.domain.FriendMember
import com.beside.groubing.domain.common.file.domain.FileInfo
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

data class FriendResponse(
    @EncryptId(ObfuscationType.FRIEND)
    val id: Long,
    @EncryptId(ObfuscationType.MEMBER)
    val memberId: Long,
    val nickname: String,
    val profileUrl: String?
) {
    companion object {
        fun of(friendMember: FriendMember): FriendResponse = FriendResponse(
            id = friendMember.friendId,
            memberId = friendMember.memberId,
            nickname = friendMember.nickname,
            profileUrl = FileInfo.urlOfOrNull(friendMember.profileFileName)
        )
    }
}

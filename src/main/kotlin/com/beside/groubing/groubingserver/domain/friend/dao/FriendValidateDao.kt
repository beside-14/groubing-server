package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import org.springframework.stereotype.Repository

@Repository
class FriendValidateDao {
    fun validateAddFriend(friends: List<Friend>) {
        if (friends.any { friend -> !friend.status.isReject()}) {
            throw FriendInputException("이미 등록된 친구이거나 친구 요청 대기 상태입니다.")
        }
    }

    fun validateAcceptOrRejectFriend(memberId: Long, friend: Friend) {
        if (!friend.isInviter(memberId)) {
            throw FriendInputException("당사자가 아니면 친구 요청을 수락하거나 거절할 수 없습니다. memberId : ${memberId}, friend: $friend")
        }
        if (!friend.status.isPending()) {
            throw FriendInputException("이미 등록된 친구이거나 친구 요청 상태가 아닙니다. status : ${friend.status}")
        }
    }
}

package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import org.springframework.stereotype.Repository

@Repository
class FriendValidateDao {
    fun validateAddFriend(friends: List<Friend>) {
        val isFriend = friends.any { friend -> friend.status.isAccept() || friend.status.isPending() }
        if (isFriend) throw FriendInputException("이미 친구이거나 친구 수락 대기 상태입니다.")
    }

    fun validateStatus(friend: Friend) {
        if (!friend.status.isPending()) throw FriendInputException("이미 처리된 친구 요청입니다.")
    }
}

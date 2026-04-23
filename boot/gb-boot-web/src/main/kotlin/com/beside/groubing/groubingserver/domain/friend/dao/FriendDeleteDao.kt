package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import org.springframework.stereotype.Repository

@Repository
class FriendDeleteDao(
    private val friendFindDao: FriendFindDao,
    private val friendRepository: FriendRepository
) {
    fun deleteAll(requesterId: Long, targetMemberId: Long) {
        val friends = friendFindDao.findByFriends(requesterId, targetMemberId)
        if (friends.isNotEmpty()) friendRepository.deleteAll(friends)
    }
}

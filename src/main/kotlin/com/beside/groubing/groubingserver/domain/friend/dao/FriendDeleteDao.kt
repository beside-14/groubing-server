package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import org.springframework.stereotype.Repository

@Repository
class FriendDeleteDao(
    private val friendFindDao: FriendFindDao,
    private val friendRepository: FriendRepository
) {
    fun deleteAll(requesterId: Long, targetMemberId: Long) {
        val friendships = friendFindDao.findByFriendships(requesterId, targetMemberId)
        if (friendships.isNotEmpty()) friendRepository.deleteAll(friendships)
    }
}

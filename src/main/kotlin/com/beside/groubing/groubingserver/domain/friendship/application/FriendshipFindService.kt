package com.beside.groubing.groubingserver.domain.friendship.application

import com.beside.groubing.groubingserver.domain.friendship.dao.FriendshipFindDao
import com.beside.groubing.groubingserver.domain.friendship.payload.response.FriendResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendshipFindService(
    private val friendshipFindDao: FriendshipFindDao
) {
    fun findById(id: Long, pageable: Pageable): Page<FriendResponse> {
        val friends = friendshipFindDao.findFriendsById(id, pageable)
        return friends.map(::FriendResponse)
    }

    fun findByIdAndNickname(id: Long, pageable: Pageable, nickname: String): Page<FriendResponse> {
        val friends = friendshipFindDao.findFriendsByIdAndNickname(id, pageable, nickname)
        return friends.map(::FriendResponse)
    }
}

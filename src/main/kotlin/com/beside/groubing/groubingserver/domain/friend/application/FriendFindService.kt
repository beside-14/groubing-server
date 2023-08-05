package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.payload.response.FriendResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendFindService(
    private val friendFindDao: FriendFindDao
) {
    fun findById(id: Long): List<FriendResponse> {
        val friends = friendFindDao.findAllById(id)
        return friends.map(::FriendResponse)
    }
}

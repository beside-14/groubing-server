package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendAcceptService(
    private val friendFindDao: FriendFindDao
) {
    fun accept(id: Long) {
        val friendship = friendFindDao.findById(id)
        friendship.status = FriendStatus.ACCEPT
    }
}

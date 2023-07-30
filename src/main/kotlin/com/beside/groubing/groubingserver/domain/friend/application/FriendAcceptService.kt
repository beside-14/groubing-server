package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.dao.FriendValidateDao
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendAcceptService(
    private val friendFindDao: FriendFindDao,
    private val friendValidateDao: FriendValidateDao
) {
    fun accept(id: Long) {
        val friend = friendFindDao.findById(id)
        friendValidateDao.validateStatus(friend)
        friend.status = FriendStatus.ACCEPT
    }
}

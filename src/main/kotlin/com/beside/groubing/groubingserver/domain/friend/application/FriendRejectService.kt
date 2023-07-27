package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendRejectService(
    private val friendFindDao: FriendFindDao,
    private val friendRepository: FriendRepository
) {
    fun reject(id: Long) {
        val friendship = friendFindDao.findById(id)
        friendship.status = FriendStatus.REJECT
        friendRepository.save(friendship)
    }
}

package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.member.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.member.domain.FriendStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberFriendAcceptService(
    private val friendRepository: FriendRepository,
    private val friendFindDao: FriendFindDao
) {
    fun accept(id: Long) {
        val friendship = friendFindDao.findById(id)
        friendship.status = FriendStatus.ACCEPT
        friendRepository.save(friendship)
    }
}

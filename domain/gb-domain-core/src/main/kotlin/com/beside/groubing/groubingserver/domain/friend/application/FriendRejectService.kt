package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendCommandRepository
import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendRejectService(
    private val friendQueryRepository: FriendQueryRepository,
    private val friendCommandRepository: FriendCommandRepository
) {
    fun reject(memberId: Long, id: Long) {
        val friend = friendQueryRepository.findOne(id)
        friendCommandRepository.update(friend.reject(memberId))
    }
}

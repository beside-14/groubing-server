package com.beside.groubing.domain.friend.application

import com.beside.groubing.domain.friend.domain.port.FriendCommandRepository
import com.beside.groubing.domain.friend.domain.port.FriendQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendAcceptService(
    private val friendQueryRepository: FriendQueryRepository,
    private val friendCommandRepository: FriendCommandRepository
) {
    fun accept(memberId: Long, id: Long) {
        val friend = friendQueryRepository.findOne(id)
        friendCommandRepository.update(friend.accept(memberId))
    }
}

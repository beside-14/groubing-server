package com.beside.groubing.groubingserver.domain.friendship.application

import com.beside.groubing.groubingserver.domain.friendship.domain.FriendshipRepository
import com.beside.groubing.groubingserver.domain.friendship.domain.FriendshipStatus
import com.beside.groubing.groubingserver.domain.friendship.exception.FriendshipInputException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendshipChangeStatusService(
    private val friendshipRepository: FriendshipRepository
) {
    fun change(id: Long, status: FriendshipStatus) {
        val friendship = friendshipRepository.findById(id).orElseThrow { FriendshipInputException("존재하지 않는 친구 요청입니다.") }
        if (!friendship.status.isPending()) throw FriendshipInputException("이미 처리된 친구 요청입니다.")
        friendship.status = status
    }
}

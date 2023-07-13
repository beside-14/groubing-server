package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendDeleteService(
    private val friendRepository: FriendRepository
) {
    fun delete(id: Long) {
        friendRepository.deleteById(id)
    }
}

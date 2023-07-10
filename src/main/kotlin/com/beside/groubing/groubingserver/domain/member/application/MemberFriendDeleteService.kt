package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.FriendRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberFriendDeleteService(
    private val friendRepository: FriendRepository
) {
    fun delete(id: Long) {
        friendRepository.deleteById(id)
    }
}

package com.beside.groubing.domain.member.application

import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberNotiUpdateService(
    private val memberQueryRepository: MemberQueryRepository,
    private val memberCommandRepository: MemberCommandRepository
) {
    fun onNotification(memberId: Long) {
        updateReceive(memberId, true)
    }

    fun offNotification(memberId: Long) {
        updateReceive(memberId, false)
    }

    private fun updateReceive(memberId: Long, receive: Boolean) {
        val member = memberQueryRepository.findById(memberId)
        memberCommandRepository.update(member.withNotificationReceive(receive))
    }
}

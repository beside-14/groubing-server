package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberNotiUpdateService(
    private val memberFindDao: MemberFindDao,
    private val memberCommandRepository: MemberCommandRepository
) {
    fun onNotification(memberId: Long) {
        updateReceive(memberId, true)
    }

    fun offNotification(memberId: Long) {
        updateReceive(memberId, false)
    }

    private fun updateReceive(memberId: Long, receive: Boolean) {
        val member = memberFindDao.findExistingMemberById(memberId)
        memberCommandRepository.update(member.withNotificationReceive(receive))
    }
}

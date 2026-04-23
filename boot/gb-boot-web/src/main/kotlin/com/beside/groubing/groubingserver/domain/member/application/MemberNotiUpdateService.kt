package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberNotiUpdateService(
    private val memberFindDao: MemberFindDao
) {
    fun onNotification(memberId: Long) {
        val member = memberFindDao.findExistingMemberById(memberId)
        member.editNotificationReceive(true)
    }

    fun offNotification(memberId: Long) {
        val member = memberFindDao.findExistingMemberById(memberId)
        member.editNotificationReceive(false)
    }
}

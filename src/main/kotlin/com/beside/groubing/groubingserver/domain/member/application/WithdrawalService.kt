package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.stereotype.Service

@Service
class WithdrawalService(
    private val memberFindDao: MemberFindDao
) {

    fun withdrawal(memberId: Long) {
        val member = memberFindDao.findExistingMemberById(memberId)
        member.withdrawal()
    }
}

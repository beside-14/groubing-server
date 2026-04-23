package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberEmailFindResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberEmailFindService(
    private val memberFindDao: MemberFindDao
) {
    fun find(email: String): MemberEmailFindResponse {
        val member = memberFindDao.findExistingMemberByEmail(email)
        return MemberEmailFindResponse(member.id, member.maskEmail())
    }
}

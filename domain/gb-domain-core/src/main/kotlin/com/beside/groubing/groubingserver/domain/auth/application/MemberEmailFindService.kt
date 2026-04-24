package com.beside.groubing.groubingserver.domain.auth.application

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberEmailFindService(
    private val memberQueryRepository: MemberQueryRepository
) {
    fun find(email: String): Member {
        return memberQueryRepository.findByEmail(email)
    }
}

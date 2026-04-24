package com.beside.groubing.groubingserver.domain.auth.application

import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberEmailFindResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberEmailFindService(
    private val memberQueryRepository: MemberQueryRepository
) {
    fun find(email: String): MemberEmailFindResponse {
        val member = memberQueryRepository.findByEmail(email)
        return MemberEmailFindResponse(member.id, member.maskEmail())
    }
}

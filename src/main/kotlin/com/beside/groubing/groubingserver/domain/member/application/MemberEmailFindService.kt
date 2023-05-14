package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberEmailFindResponse
import org.springframework.stereotype.Service

@Service
class MemberEmailFindService(
    private val memberRepository: MemberRepository
) {
    fun find(email: String): MemberEmailFindResponse {
        val member = memberRepository.findByEmail(email) ?: throw MemberInputException("존재하지 않는 유저 입니다.")
        return MemberEmailFindResponse(member.id, member.maskEmail())
    }
}

package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.MemberMapper
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.command.SignUpCommand
import com.beside.groubing.groubingserver.domain.member.payload.response.SignUpResponse
import org.springframework.stereotype.Service

@Service
class SignUpService(
        private val memberRepository: MemberRepository,
        private val memberMapper: MemberMapper
) {
    fun signUp(signUpCommand: SignUpCommand): SignUpResponse {
        // 이미 존재하는 회원인지
        if (memberRepository.existsByEmail(signUpCommand.email)) {
            throw MemberInputException("이미 가입된 email 주소입니다.")
        }
        val savedMember = memberRepository.save(memberMapper.toMember(signUpCommand))
        return SignUpResponse(savedMember.email)
    }
}
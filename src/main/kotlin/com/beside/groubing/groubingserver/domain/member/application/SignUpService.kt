package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.MemberMapper
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.command.SignUpCommand
import com.beside.groubing.groubingserver.domain.member.payload.response.LoginResponse
import com.beside.groubing.groubingserver.global.domain.security.JwtProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SignUpService(
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper
) {
    fun signUp(signUpCommand: SignUpCommand): LoginResponse {
        // 이미 존재하는 회원인지
        if (memberRepository.existsByEmail(signUpCommand.email)) {
            throw MemberInputException("이미 가입된 email 주소입니다.")
        }
        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(signUpCommand.nickname)) {
            throw MemberInputException("이미 사용 중인 닉네임 입니다.")
        }
        // TODO(비속어 체크)
        val savedMember = memberRepository.save(memberMapper.toMember(signUpCommand))
        val token = JwtProvider.createToken(savedMember.id, savedMember.email, savedMember.role)
        return LoginResponse(savedMember.id, savedMember.email, token)
    }
}

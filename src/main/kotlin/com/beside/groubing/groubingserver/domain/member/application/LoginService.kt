package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.domain.MemberType
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.command.LoginCommand
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberResponse
import com.beside.groubing.groubingserver.global.domain.security.JwtProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LoginService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun login(loginCommand: LoginCommand): MemberResponse {
        // 아이디가 존재하는지
        val member = memberRepository.findByEmailAndMemberType(loginCommand.email, MemberType.CLASSIC)
            ?: throw MemberInputException("존재하지 않는 이메일 입니다.: $loginCommand.email")
        // 패스워드가 일치하는지
        member.matches(loginCommand.password, passwordEncoder)
        member.editFcmToken(loginCommand.fcmToken)
        return MemberResponse(
            member.id,
            member.email!!,
            member.nickname,
            member.profile?.url,
            JwtProvider.createToken(member.id, member.role),
            member.notificationReceive
        )
    }
}

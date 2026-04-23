package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.auth.application.command.LoginCommand
import com.beside.groubing.groubingserver.domain.auth.domain.port.PasswordEncryptor
import com.beside.groubing.groubingserver.domain.auth.domain.port.TokenManager
import com.beside.groubing.groubingserver.domain.member.domain.MemberType
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LoginService(
    private val memberQueryRepository: MemberQueryRepository,
    private val memberCommandRepository: MemberCommandRepository,
    private val passwordEncryptor: PasswordEncryptor,
    private val tokenManager: TokenManager
) {
    fun login(loginCommand: LoginCommand): MemberResponse {
        val member = memberQueryRepository.findByEmailAndMemberType(loginCommand.email, MemberType.CLASSIC)
            ?: throw MemberInputException("존재하지 않는 이메일 입니다.: ${loginCommand.email}")
        if (!passwordEncryptor.matches(loginCommand.password, member.password)) {
            throw MemberInputException("비밀번호가 일치하지 않습니다.")
        }
        val updated = memberCommandRepository.update(member.withFcmToken(loginCommand.fcmToken))
        return MemberResponse(
            updated.id,
            updated.email!!,
            updated.nickname,
            updated.profileUrl,
            tokenManager.generateAccessToken(updated.id, updated.role.name),
            updated.notificationReceive
        )
    }
}

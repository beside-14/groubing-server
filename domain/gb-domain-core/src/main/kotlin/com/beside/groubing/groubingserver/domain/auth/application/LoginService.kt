package com.beside.groubing.groubingserver.domain.auth.application

import com.beside.groubing.groubingserver.domain.auth.application.command.LoginCommand
import com.beside.groubing.groubingserver.domain.auth.domain.AuthenticatedMember
import com.beside.groubing.groubingserver.domain.auth.domain.PasswordVerifier
import com.beside.groubing.groubingserver.domain.auth.domain.port.TokenManager
import com.beside.groubing.groubingserver.domain.member.domain.MemberType
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LoginService(
    private val memberQueryRepository: MemberQueryRepository,
    private val memberCommandRepository: MemberCommandRepository,
    private val passwordVerifier: PasswordVerifier,
    private val tokenManager: TokenManager
) {
    fun login(loginCommand: LoginCommand): AuthenticatedMember {
        val member = memberQueryRepository.findByEmailAndMemberType(loginCommand.email, MemberType.CLASSIC)
        passwordVerifier.verify(loginCommand.password, member.password)
        val updated = memberCommandRepository.update(member.withFcmToken(loginCommand.fcmToken))
        return AuthenticatedMember(updated, tokenManager.generateAccessToken(updated.id, updated.role.name))
    }
}

package com.beside.groubing.domain.auth.application

import com.beside.groubing.domain.auth.application.command.SignUpCommand
import com.beside.groubing.domain.auth.domain.AuthenticatedMember
import com.beside.groubing.domain.auth.domain.SignUpValidator
import com.beside.groubing.domain.auth.domain.port.PasswordEncryptor
import com.beside.groubing.domain.auth.domain.port.TokenManager
import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SignUpService(
    private val memberCommandRepository: MemberCommandRepository,
    private val signUpValidator: SignUpValidator,
    private val passwordEncryptor: PasswordEncryptor,
    private val tokenManager: TokenManager
) {
    fun signUp(signUpCommand: SignUpCommand): AuthenticatedMember {
        val newMember = signUpCommand.toNewMember(passwordEncryptor)
        signUpValidator.validate(newMember)
        val savedMember = memberCommandRepository.save(newMember)
        return AuthenticatedMember(savedMember, tokenManager.generateAccessToken(savedMember.id, savedMember.role.name))
    }
}

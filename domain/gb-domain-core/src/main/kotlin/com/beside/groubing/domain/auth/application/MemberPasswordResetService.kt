package com.beside.groubing.domain.auth.application

import com.beside.groubing.domain.auth.domain.PasswordVerifier
import com.beside.groubing.domain.auth.domain.port.PasswordEncryptor
import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberPasswordResetService(
    private val memberQueryRepository: MemberQueryRepository,
    private val memberCommandRepository: MemberCommandRepository,
    private val passwordVerifier: PasswordVerifier,
    private val passwordEncryptor: PasswordEncryptor
) {
    fun reset(id: Long, beforePassword: String, afterPassword: String) {
        val member = memberQueryRepository.findById(id)
        passwordVerifier.verify(beforePassword, member.password)
        memberCommandRepository.update(member.withPassword(passwordEncryptor.encode(afterPassword)))
    }
}

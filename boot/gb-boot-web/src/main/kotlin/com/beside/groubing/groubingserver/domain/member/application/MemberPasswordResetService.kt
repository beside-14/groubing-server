package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.auth.domain.PasswordVerifier
import com.beside.groubing.groubingserver.domain.auth.domain.port.PasswordEncryptor
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberPasswordResetService(
    private val memberFindDao: MemberFindDao,
    private val memberCommandRepository: MemberCommandRepository,
    private val passwordVerifier: PasswordVerifier,
    private val passwordEncryptor: PasswordEncryptor
) {
    fun reset(id: Long, beforePassword: String, afterPassword: String) {
        val member = memberFindDao.findExistingMemberById(id)
        passwordVerifier.verify(beforePassword, member.password)
        memberCommandRepository.update(member.withPassword(passwordEncryptor.encode(afterPassword)))
    }
}

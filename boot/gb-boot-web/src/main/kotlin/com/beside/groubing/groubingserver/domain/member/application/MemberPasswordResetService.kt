package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.auth.domain.port.PasswordEncryptor
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberPasswordResetService(
    private val memberFindDao: MemberFindDao,
    private val memberCommandRepository: MemberCommandRepository,
    private val passwordEncryptor: PasswordEncryptor
) {
    fun reset(id: Long, beforePassword: String, afterPassword: String) {
        val member = memberFindDao.findExistingMemberById(id)
        if (!passwordEncryptor.matches(beforePassword, member.password)) {
            throw MemberInputException("비밀번호가 일치하지 않습니다.")
        }
        memberCommandRepository.update(member.withPassword(passwordEncryptor.encode(afterPassword)))
    }
}

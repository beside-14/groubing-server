package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberPasswordResetService(
    private val memberFindDao: MemberFindDao,
    private val passwordEncoder: PasswordEncoder
) {
    fun reset(id: Long, password: String) {
        val member = memberFindDao.findExistingMemberById(id)
        val encodedPassword = passwordEncoder.encode(password)
        member.editPassword(encodedPassword)
    }
}
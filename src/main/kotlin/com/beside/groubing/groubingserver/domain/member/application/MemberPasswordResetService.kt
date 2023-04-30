package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberPasswordResetService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun reset(id: Long, password: String) {
        val member = memberRepository.findById(id).orElseThrow { MemberInputException("존재하지 않는 유저 입니다.") }
        val encodedPassword = passwordEncoder.encode(password)
        member.editPassword(encodedPassword)
    }
}

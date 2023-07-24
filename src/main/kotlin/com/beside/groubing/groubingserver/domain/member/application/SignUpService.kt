package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberValidateDao
import com.beside.groubing.groubingserver.domain.member.domain.MemberMapper
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.payload.command.SignUpCommand
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberResponse
import com.beside.groubing.groubingserver.global.domain.security.JwtProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SignUpService(
    private val memberRepository: MemberRepository,
    private val memberValidateDao: MemberValidateDao,
    private val memberMapper: MemberMapper
) {
    fun signUp(signUpCommand: SignUpCommand): MemberResponse {
        memberValidateDao.validateDuplicateEmail(signUpCommand.email)
        memberValidateDao.validateDuplicateNickname(signUpCommand.nickname)
        // TODO(비속어 체크)
        val savedMember = memberRepository.save(memberMapper.toMember(signUpCommand))
        val token = JwtProvider.createToken(savedMember.id, savedMember.email, savedMember.role)
        return MemberResponse(savedMember.id, savedMember.email, token)
    }
}

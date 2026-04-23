package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.auth.application.command.SignUpCommand
import com.beside.groubing.groubingserver.domain.auth.domain.port.PasswordEncryptor
import com.beside.groubing.groubingserver.domain.auth.domain.port.TokenManager
import com.beside.groubing.groubingserver.domain.member.dao.MemberValidateDao
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberResponse
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SignUpService(
    private val memberCommandRepository: MemberCommandRepository,
    private val memberValidateDao: MemberValidateDao,
    private val passwordEncryptor: PasswordEncryptor,
    private val tokenManager: TokenManager
) {
    fun signUp(signUpCommand: SignUpCommand): MemberResponse {
        memberValidateDao.validateDuplicateEmail(signUpCommand.email)
        memberValidateDao.validateDuplicateNickname(signUpCommand.nickname)
        try {
            val savedMember = memberCommandRepository.save(signUpCommand.toNewMember(passwordEncryptor))
            val token = tokenManager.generateAccessToken(savedMember.id, savedMember.role.name)
            return MemberResponse(
                savedMember.id,
                savedMember.email!!,
                savedMember.nickname,
                savedMember.profileUrl,
                token,
                savedMember.notificationReceive
            )
        } catch (e: DataIntegrityViolationException) {
            throw MemberInputException("중복된 email / 닉네임 입니다.")
        }
    }
}

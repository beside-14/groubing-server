package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.dao.MemberValidateDao
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberNicknameEditService(
    private val memberFindDao: MemberFindDao,
    private val memberValidateDao: MemberValidateDao,
    private val memberCommandRepository: MemberCommandRepository
) {
    @Transactional
    fun edit(id: Long, nickname: String) {
        memberValidateDao.validateDuplicateNickname(nickname)
        val member = memberFindDao.findExistingMemberById(id)
        memberCommandRepository.update(member.withNickname(nickname))
    }
}

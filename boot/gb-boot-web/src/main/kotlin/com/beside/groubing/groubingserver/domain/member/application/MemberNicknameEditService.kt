package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.dao.MemberValidateDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberNicknameEditService(
    private val memberFindDao: MemberFindDao,
    private val memberValidateDao: MemberValidateDao
) {
    @Transactional
    fun edit(id: Long, nickname: String) {
        val member = memberFindDao.findExistingMemberById(id)
        memberValidateDao.validateDuplicateNickname(nickname)
        // TODO(비속어 체크)
        member.editNickname(nickname)
    }
}

package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.global.domain.file.application.FileProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberProfileDeleteService(
    private val memberFindDao: MemberFindDao
) {
    fun delete(id: Long) {
        val member = memberFindDao.findExistingMemberById(id)
        FileProvider.delete(member.profile)
        member.deleteProfile()
    }
}

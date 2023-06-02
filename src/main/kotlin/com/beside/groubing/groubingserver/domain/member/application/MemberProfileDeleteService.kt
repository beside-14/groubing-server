package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.global.domain.file.application.FileProvider
import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberProfileDeleteService(
    private val memberFindDao: MemberFindDao,
    private val fileInfoRepository: FileInfoRepository
) {
    fun delete(id: Long) {
        val member = memberFindDao.findExistingMemberById(id)
        val profile = member.profile
        if (profile != null) {
            FileProvider.delete(profile)
            fileInfoRepository.delete(profile)
        }
        member.deleteProfile()
    }
}

package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.global.domain.file.application.FileProvider
import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo
import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class MemberProfileEditService(
    private val memberFindDao: MemberFindDao,
    private val fileInfoRepository: FileInfoRepository
) {
    fun deleteAndEdit(id: Long, profile: MultipartFile): String {
        val member = memberFindDao.findExistingMemberById(id)
        delete(member.profile)
        edit(member, profile)
        return member.profile!!.url
    }

    private fun delete(beforeProfile: FileInfo?) {
        if (beforeProfile != null) {
            FileProvider.delete(beforeProfile)
            fileInfoRepository.delete(beforeProfile)
        }
    }

    private fun edit(member: Member, newProfileFile: MultipartFile) {
        val newProfile = FileProvider.upload(newProfileFile)
        member.editProfile(newProfile)
    }
}

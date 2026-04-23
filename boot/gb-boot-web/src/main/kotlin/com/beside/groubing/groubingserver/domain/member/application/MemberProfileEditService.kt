package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.entity.MemberEntity
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.repository.MemberJpaRepository
import com.beside.groubing.groubingserver.global.domain.file.application.FileProvider
import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class MemberProfileEditService(
    private val memberJpaRepository: MemberJpaRepository
) {
    fun edit(id: Long, profile: MultipartFile): String {
        val member = memberJpaRepository.findById(id)
            .orElseThrow { MemberInputException("존재하지 않는 유저 입니다.") }
        deletePreviousProfile(member.profile)
        uploadNewProfile(member, profile)
        return member.profile!!.url
    }

    private fun deletePreviousProfile(beforeProfile: FileInfo?) {
        if (beforeProfile != null) {
            FileProvider.delete(beforeProfile)
        }
    }

    private fun uploadNewProfile(member: MemberEntity, newProfileFile: MultipartFile) {
        val newProfile = FileProvider.upload(newProfileFile)
        member.editProfile(newProfile)
    }
}

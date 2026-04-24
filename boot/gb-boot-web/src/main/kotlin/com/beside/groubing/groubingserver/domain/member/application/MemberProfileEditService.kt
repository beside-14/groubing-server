package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.groubingserver.global.domain.file.application.FileProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class MemberProfileEditService(
    private val memberCommandRepository: MemberCommandRepository
) {
    fun edit(id: Long, profile: MultipartFile): String {
        val newProfile = FileProvider.upload(profile)
        val previousProfile = memberCommandRepository.editProfile(id, newProfile)
        previousProfile?.let { FileProvider.delete(it) }
        return newProfile.url
    }
}

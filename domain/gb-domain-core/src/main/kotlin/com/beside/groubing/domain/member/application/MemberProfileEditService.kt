package com.beside.groubing.domain.member.application

import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.common.file.application.FileStorage
import com.beside.groubing.domain.common.file.domain.FileInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberProfileEditService(
    private val memberCommandRepository: MemberCommandRepository
) {
    fun edit(id: Long, newProfile: FileInfo): String {
        val previousProfile = memberCommandRepository.editProfileOrNull(id, newProfile)
        previousProfile?.let { FileStorage.delete(it) }
        return newProfile.url
    }
}

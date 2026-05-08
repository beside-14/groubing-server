package com.beside.groubing.domain.member.application

import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.global.domain.file.application.FileStorage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberProfileDeleteService(
    private val memberCommandRepository: MemberCommandRepository
) {
    fun delete(id: Long) {
        val previousProfile = memberCommandRepository.deleteProfileOrNull(id)
        previousProfile?.let { FileStorage.delete(it) }
    }
}

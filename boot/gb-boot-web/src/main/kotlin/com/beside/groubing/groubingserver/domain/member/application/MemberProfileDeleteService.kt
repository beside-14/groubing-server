package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.groubingserver.global.domain.file.application.FileProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberProfileDeleteService(
    private val memberCommandRepository: MemberCommandRepository
) {
    fun delete(id: Long) {
        val previousProfile = memberCommandRepository.deleteProfile(id)
        previousProfile?.let { FileProvider.delete(it) }
    }
}

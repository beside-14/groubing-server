package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.repository.MemberJpaRepository
import com.beside.groubing.groubingserver.global.domain.file.application.FileProvider
import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberProfileDeleteService(
    private val memberJpaRepository: MemberJpaRepository,
    private val fileInfoRepository: FileInfoRepository
) {
    fun delete(id: Long) {
        val member = memberJpaRepository.findById(id)
            .orElseThrow { MemberInputException("존재하지 않는 유저 입니다.") }
        val profile = member.profile
        if (profile != null) {
            FileProvider.delete(profile)
            fileInfoRepository.delete(profile)
        }
        member.deleteProfile()
    }
}

package com.beside.groubing.groubingserver.domain.member.dao

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.Members
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.repository.MemberJpaRepository
import org.springframework.stereotype.Repository

@Repository
class MemberFindDao(
    private val memberJpaRepository: MemberJpaRepository
) {
    fun findAllById(ids: List<Long>): Members {
        return Members(memberJpaRepository.findAllById(ids).map { it.toDomain() })
    }

    fun findPushNotificationMembers(ids: List<Long>): List<Member> {
        return memberJpaRepository.findByFcmTokenNotNullAndNotificationReceiveIsTrue().map { it.toDomain() }
    }

    fun findExistingMemberById(id: Long): Member {
        return memberJpaRepository.findById(id).map { it.toDomain() }.orElseThrow {
            MemberInputException("존재하지 않는 유저 입니다.")
        }
    }

    fun findExistingMemberByEmail(email: String): Member {
        return memberJpaRepository.findByEmail(email)?.toDomain()
            ?: throw MemberInputException("존재하지 않는 유저 입니다.")
    }
}

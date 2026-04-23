package com.beside.groubing.groubingserver.domain.member.repository

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberType
import com.beside.groubing.groubingserver.domain.member.domain.NewMember
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.groubingserver.domain.member.entity.MemberEntity
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryAdapter(
    private val memberJpaRepository: MemberJpaRepository
) : MemberCommandRepository, MemberQueryRepository {
    override fun save(newMember: NewMember): Member {
        return memberJpaRepository.save(MemberEntity.from(newMember)).toDomain()
    }

    override fun update(member: Member): Member {
        val entity = memberJpaRepository.findById(member.id)
            .orElseThrow { MemberInputException("존재하지 않는 유저 입니다.") }
        entity.applyChanges(member)
        return entity.toDomain()
    }

    override fun findById(id: Long): Member? {
        return memberJpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    override fun findByEmail(email: String): Member? {
        return memberJpaRepository.findByEmail(email)?.toDomain()
    }

    override fun findByEmailAndMemberType(email: String, memberType: MemberType): Member? {
        return memberJpaRepository.findByEmailAndMemberType(email, memberType)?.toDomain()
    }

    override fun findPushRecipients(): List<Member> {
        return memberJpaRepository.findByFcmTokenNotNullAndNotificationReceiveIsTrue().map { it.toDomain() }
    }

    override fun existsByEmail(email: String): Boolean {
        return memberJpaRepository.existsByEmail(email)
    }

    override fun existsByNickname(nickname: String): Boolean {
        return memberJpaRepository.existsByNickname(nickname)
    }

    override fun countByIdIn(ids: Collection<Long>): Int {
        return memberJpaRepository.countByIdIn(ids)
    }
}

package com.beside.groubing.domain.member.repository

import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.domain.NewMember
import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.member.entity.MemberEntity
import com.beside.groubing.domain.member.exception.MemberInputException
import com.beside.groubing.domain.common.file.domain.FileInfo
import com.beside.groubing.domain.common.file.entity.FileInfoEntity
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryAdapter(
    private val memberJpaRepository: MemberJpaRepository
) : MemberCommandRepository, MemberQueryRepository {
    override fun save(newMember: NewMember): Member {
        return memberJpaRepository.save(MemberEntity.from(newMember)).toDomain()
    }

    override fun update(member: Member): Member {
        val entity = findEntityById(member.id)
        entity.applyChanges(member)
        return entity.toDomain()
    }

    override fun editProfileOrNull(memberId: Long, newProfile: FileInfo): FileInfo? {
        val entity = findEntityById(memberId)
        val previous = entity.profile?.toDomain()
        entity.editProfile(FileInfoEntity.from(newProfile))
        return previous
    }

    override fun deleteProfileOrNull(memberId: Long): FileInfo? {
        val entity = findEntityById(memberId)
        val previous = entity.profile?.toDomain()
        entity.deleteProfile()
        return previous
    }

    override fun findById(id: Long): Member {
        return findEntityById(id).toDomain()
    }

    override fun findByEmail(email: String): Member {
        return memberJpaRepository.findByEmailAndActiveTrue(email)?.toDomain()
            ?: throw MemberInputException("존재하지 않는 유저 입니다.")
    }

    override fun findByEmailAndMemberType(email: String, memberType: MemberType): Member {
        return memberJpaRepository.findByEmailAndMemberTypeAndActiveTrue(email, memberType)?.toDomain()
            ?: throw MemberInputException("존재하지 않는 이메일 입니다.: $email")
    }

    override fun findAllSortedByNickname(): List<Member> {
        return memberJpaRepository.findAllByActiveTrue(Sort.by(Sort.Direction.ASC, "nickname"))
            .map { it.toDomain() }
    }

    override fun findAllSortedByNicknameExcluding(excludedIds: Set<Long>): List<Member> {
        return memberJpaRepository.findAllByIdNotInAndActiveTrue(excludedIds, Sort.by(Sort.Direction.ASC, "nickname"))
            .map { it.toDomain() }
    }

    override fun findAll(ids: Collection<Long>): List<Member> {
        return memberJpaRepository.findAllByIdInAndActiveTrue(ids).map { it.toDomain() }
    }

    override fun existsByEmail(email: String): Boolean {
        return memberJpaRepository.existsByEmailAndActiveTrue(email)
    }

    override fun existsByNickname(nickname: String): Boolean {
        return memberJpaRepository.existsByNicknameAndActiveTrue(nickname)
    }

    override fun count(ids: Collection<Long>): Int {
        return memberJpaRepository.countByIdInAndActiveTrue(ids)
    }

    private fun findEntityById(id: Long): MemberEntity {
        return memberJpaRepository.findByIdAndActiveTrue(id)
            ?: throw MemberInputException("존재하지 않는 유저 입니다.")
    }
}

package com.beside.groubing.domain.member.repository

import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.NewMember
import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.member.entity.MemberEntity
import com.beside.groubing.domain.member.exception.MemberInputException
import com.beside.groubing.domain.common.file.domain.FileInfo
import com.beside.groubing.domain.common.file.entity.FileInfoEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MemberRepositoryAdapter(
    private val memberJpaRepository: MemberJpaRepository
) : MemberCommandRepository, MemberQueryRepository {
    override fun save(newMember: NewMember): Member {
        return try {
            memberJpaRepository.save(MemberEntity.from(newMember)).toDomain()
        } catch (e: DataIntegrityViolationException) {
            throw MemberInputException("중복된 아이디 / 닉네임 입니다.")
        }
    }

    override fun update(member: Member): Member {
        val entity = findActiveEntityById(member.id)
        entity.applyChanges(member)
        return entity.toDomain()
    }

    override fun withdraw(memberId: Long, now: LocalDateTime) {
        findActiveEntityById(memberId).withdraw(now)
    }

    override fun tombstone(memberId: Long, now: LocalDateTime): FileInfo? {
        val entity = findEntityById(memberId)
        val previousProfile = entity.profile?.toDomain()
        entity.tombstone(now)
        return previousProfile
    }

    override fun hardDelete(memberId: Long): FileInfo? {
        val entity = findEntityById(memberId)
        val previousProfile = entity.profile?.toDomain()
        memberJpaRepository.delete(entity)
        return previousProfile
    }

    override fun editProfileOrNull(memberId: Long, newProfile: FileInfo): FileInfo? {
        val entity = findActiveEntityById(memberId)
        val previous = entity.profile?.toDomain()
        entity.editProfile(FileInfoEntity.from(newProfile))
        return previous
    }

    override fun deleteProfileOrNull(memberId: Long): FileInfo? {
        val entity = findActiveEntityById(memberId)
        val previous = entity.profile?.toDomain()
        entity.deleteProfile()
        return previous
    }

    override fun findById(id: Long): Member {
        return findEntityById(id).toDomain()
    }

    override fun findExpiredMemberIds(threshold: LocalDateTime): List<Long> {
        return memberJpaRepository.findExpiredMemberIds(threshold)
    }

    override fun findActiveById(id: Long): Member {
        return findActiveEntityById(id).toDomain()
    }

    override fun findOneByLoginId(loginId: String): Member {
        return memberJpaRepository.findByLoginIdAndActiveTrue(loginId)?.toDomain()
            ?: throw MemberInputException("존재하지 않는 아이디 입니다.: $loginId")
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
        return memberJpaRepository.findAllById(ids).map { it.toDomain() }
    }

    override fun findAllActive(ids: Collection<Long>): List<Member> {
        return memberJpaRepository.findAllByIdInAndActiveTrue(ids).map { it.toDomain() }
    }

    override fun existsByLoginId(loginId: String): Boolean {
        return memberJpaRepository.existsByLoginIdAndActiveTrue(loginId)
    }

    override fun existsByNickname(nickname: String): Boolean {
        return memberJpaRepository.existsByNicknameAndActiveTrue(nickname)
    }

    override fun count(ids: Collection<Long>): Int {
        return memberJpaRepository.countByIdInAndActiveTrue(ids)
    }

    private fun findActiveEntityById(id: Long): MemberEntity {
        return memberJpaRepository.findByIdAndActiveTrue(id) ?: throw memberNotFound()
    }

    private fun findEntityById(id: Long): MemberEntity {
        return memberJpaRepository.findById(id).orElseThrow { memberNotFound() }
    }

    private fun memberNotFound() = MemberInputException("존재하지 않는 유저 입니다.")
}

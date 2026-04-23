package com.beside.groubing.groubingserver.domain.blockedmember.dao

import com.beside.groubing.groubingserver.aMember
import com.beside.groubing.groubingserver.config.QuerydslConfig
import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.blockedmember.entity.BlockedMemberEntity
import com.beside.groubing.groubingserver.domain.blockedmember.exception.BlockedMemberInputException
import com.beside.groubing.groubingserver.domain.blockedmember.repository.BlockedMemberJpaRepository
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.persistence.LocalPersistenceTest
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import org.springframework.context.annotation.Import

@LocalPersistenceTest
@Import(QuerydslConfig::class, BlockedMemberValidateDao::class)
class BlockedMemberValidateDaoTest(
    private val blockedMemberValidateDao: BlockedMemberValidateDao,
    private val blockedMemberJpaRepository: BlockedMemberJpaRepository,
    private val memberRepository: MemberRepository
) : FunSpec({
    beforeEach {
        val members =
            memberRepository.saveAll(listOf(aMember(Arb.long(1L..10L).single()), aMember(Arb.long(11L..20L).single())))
        blockedMemberJpaRepository.save(
            BlockedMemberEntity(requesterId = members[0].id, targetMemberId = members[1].id)
        )
    }

    test("당사자 혹은 차단 대상자 간 차단 여부 확인") {
        val entity = blockedMemberJpaRepository.findAll().first()
        shouldThrow<BlockedMemberInputException> {
            blockedMemberValidateDao.validateEachOther(entity.requesterId, entity.targetMemberId)
        }

        shouldNotThrow<BlockedMemberInputException> {
            blockedMemberValidateDao.validateEachOther(entity.requesterId, entity.targetMemberId.inc())
        }
    }

    test("차단 대상자 기존 차단 여부 확인") {
        val entity = blockedMemberJpaRepository.findAll().first()
        shouldThrow<BlockedMemberInputException> {
            blockedMemberValidateDao.validateBlockMember(entity.requesterId, entity.targetMemberId)
        }

        shouldNotThrow<BlockedMemberInputException> {
            blockedMemberValidateDao.validateBlockMember(entity.requesterId, entity.targetMemberId.inc())
        }
    }

    test("차단 해제 가능 여부 확인") {
        val entity = blockedMemberJpaRepository.findAll().first()
        val blockedMember: BlockedMember = entity.toDomain()
        shouldThrow<BlockedMemberInputException> {
            blockedMemberValidateDao.validateUnblock(blockedMember.requesterId.inc(), blockedMember)
        }

        shouldNotThrow<BlockedMemberInputException> {
            blockedMemberValidateDao.validateUnblock(blockedMember.requesterId, blockedMember)
        }
    }
})

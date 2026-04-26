package com.beside.groubing.domain.friend.application

import com.beside.groubing.aMember
import com.beside.groubing.config.QuerydslConfig
import com.beside.groubing.domain.blockedmember.dao.BlockedMemberFindDao
import com.beside.groubing.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.domain.blockedmember.exception.BlockedMemberInputException
import com.beside.groubing.domain.blockedmember.repository.BlockedMemberRepositoryAdapter
import com.beside.groubing.domain.friend.domain.Friend
import com.beside.groubing.domain.friend.domain.FriendAddValidator
import com.beside.groubing.domain.friend.domain.FriendStatus
import com.beside.groubing.domain.friend.dao.FriendFindDao
import com.beside.groubing.domain.friend.entity.FriendEntity
import com.beside.groubing.domain.friend.exception.FriendInputException
import com.beside.groubing.domain.friend.repository.FriendJpaRepository
import com.beside.groubing.domain.friend.repository.FriendRepositoryAdapter
import com.beside.groubing.domain.member.exception.MemberInputException
import com.beside.groubing.domain.member.repository.MemberJpaRepository
import com.beside.groubing.domain.member.repository.MemberRepositoryAdapter
import com.beside.groubing.persistence.LocalPersistenceTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import org.springframework.context.annotation.Import

@LocalPersistenceTest
@Import(
    QuerydslConfig::class,
    FriendAddService::class,
    FriendAddValidator::class,
    FriendRepositoryAdapter::class,
    FriendFindDao::class,
    MemberRepositoryAdapter::class,
    BlockedMemberRepositoryAdapter::class,
    BlockedMemberFindDao::class
)
class FriendAddServiceTest(
    private val friendAddService: FriendAddService,
    private val friendJpaRepository: FriendJpaRepository,
    private val memberRepository: MemberJpaRepository,
    private val blockedMemberRepository: BlockedMemberRepository
) : FunSpec({

    context("친구 요청 시") {
        memberRepository.saveAll((1L..10L).map { aMember(it) })
        val members = memberRepository.findAll()
        val inviter = members.first()!!
        val invitee = members.last()!!

        test("상대방이 존재하지 않는 회원인 경우") {
            shouldThrow<MemberInputException> { friendAddService.add(inviter.id, Arb.long(1000L..10000L).single()) }
        }

        test("내가 상대방에게 이미 요청한 경우") {
            friendAddService.add(inviter.id, invitee.id)
            shouldThrow<FriendInputException> { friendAddService.add(inviter.id, invitee.id) }
        }

        test("상대방이 내게 이미 요청한 경우") {
            friendAddService.add(invitee.id, inviter.id)
            shouldThrow<FriendInputException> { friendAddService.add(inviter.id, invitee.id) }
        }

        test("이미 친구인 경우") {
            val entity = FriendEntity(inviterId = inviter.id, inviteeId = invitee.id)
            entity.applyStatus(Friend.of(0L, inviter.id, invitee.id, FriendStatus.ACCEPT))
            friendJpaRepository.save(entity)
            shouldThrow<FriendInputException> { friendAddService.add(inviter.id, invitee.id) }
        }

        test("내가 나에게 요청하는 경우") {
            shouldThrow<FriendInputException> { friendAddService.add(inviter.id, inviter.id) }
        }

        test("차단한 친구에게 요청하는 경우") {
            blockedMemberRepository.save(BlockedMember.create(inviter.id, invitee.id))
            shouldThrow<BlockedMemberInputException> { friendAddService.add(inviter.id, invitee.id) }
        }

        test("상대방이 나를 차단한 경우") {
            blockedMemberRepository.save(BlockedMember.create(invitee.id, inviter.id))
            shouldThrow<BlockedMemberInputException> { friendAddService.add(inviter.id, invitee.id) }
        }
    }
})

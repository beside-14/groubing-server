package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.aMember
import com.beside.groubing.groubingserver.config.QuerydslConfig
import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberValidateDao
import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.blockedmember.exception.BlockedMemberInputException
import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.dao.FriendValidateDao
import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.persistence.LocalPersistenceTest
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
    FriendAcceptService::class,
    BlockedMemberValidateDao::class,
    FriendValidateDao::class,
    FriendFindDao::class,
    MemberFindDao::class
)
class FriendAddServiceTest(
    private val friendAddService: FriendAddService,
    private val friendRepository: FriendRepository,
    private val memberRepository: MemberRepository,
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
            val friend = Friend.create(inviter, invitee)
            friend.status = FriendStatus.ACCEPT
            friendRepository.save(friend)
            shouldThrow<FriendInputException> { friendAddService.add(inviter.id, invitee.id) }
        }

        test("내가 나에게 요청하는 경우") {
            shouldThrow<FriendInputException> { friendAddService.add(inviter.id, inviter.id) }
        }

        test("차단한 친구에게 요청하는 경우") {
            blockedMemberRepository.save(BlockedMember.create(inviter, invitee))
            shouldThrow<BlockedMemberInputException> { friendAddService.add(inviter.id, invitee.id) }
        }

        test("상대방이 나를 차단한 경우") {
            blockedMemberRepository.save(BlockedMember.create(invitee, inviter))
            shouldThrow<BlockedMemberInputException> { friendAddService.add(inviter.id, invitee.id) }
        }
    }
})

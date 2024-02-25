package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.aMember
import com.beside.groubing.groubingserver.config.QuerydslConfig
import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.persistence.LocalPersistenceTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.single
import org.springframework.context.annotation.Import

@LocalPersistenceTest
@Import(QuerydslConfig::class, FriendFindDao::class)
class FriendFindDaoTest(
    private val friendFindDao: FriendFindDao,
    private val friendRepository: FriendRepository,
    private val memberRepository: MemberRepository
) : FunSpec({

    var memberId: Long = 0L

    beforeEach {
        memberRepository.saveAllAndFlush(
            Arb.set(Arb.long(1L..100L).map { id -> aMember(id) }, 10..100)
                .single()
        )
        val members = memberRepository.findAll()
        members.sortBy { member -> member.id }

        friendRepository.saveAllAndFlush(
            (0..<members.size - 1).map { i ->
                val friend = Friend.create(members[i], members[i + 1])
                if (members[i + 1].id % 2 == 0L) friend.status = FriendStatus.ACCEPT
                friend
            }
        )
        val friends = friendRepository.findAll()
        friends.sortBy { friend -> friend.id }

        memberId = friends.filter { friend -> friend.status.isPending() }.random().invitee.id
    }

    test("수락한 친구 목록 조회") {
        val allFriends = friendRepository.findAll()
        allFriends.sortBy { friend -> friend.id }
        allFriends.forEach { friend -> println("inviterId : ${friend.inviter.id} / inviteeId : ${friend.invitee.id} / status : ${friend.status}") }
        val friends = friendFindDao.findAllByInviterIdOrInviteeId(memberId)
        friends.size shouldBe 1
    }

    test("모든 친구 요청 목록 조회") {
        val friendRequests = friendFindDao.findAllByInviteeId(memberId)
        friendRequests.size shouldBe 1
    }
})

package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.aMember
import com.beside.groubing.groubingserver.config.QuerydslConfig
import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.persistence.LocalPersistenceTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.single
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@LocalPersistenceTest
@Import(QuerydslConfig::class, FriendFindDao::class)
class FriendFindDaoTest(
    private val friendFindDao: FriendFindDao,
    private val friendRepository: FriendRepository,
    private val memberRepository: MemberRepository
) : FunSpec({

    var inviteeId: Long = 0L

    beforeEach {
        val members = memberRepository.saveAll(
            Arb.set(Arb.long(1L..100L).map { id -> aMember(id) }, 10..100)
                .single()
        )
        val friends = friendRepository.saveAllAndFlush(
            (0..<members.size - 1).map { id ->
                val friend = Friend.create(members[id], members[id + 1])
                if (id % 2 == 0) friend.status = FriendStatus.ACCEPT
                friend
            }
        )
        inviteeId = friends.filter { friend -> friend.status.isPending() }.random().invitee.id
    }

    test("수락한 친구 목록 조회") {
        val friends = friendFindDao.findAllByInviterIdOrInviteeId(inviteeId)
        if (inviteeId / 2 == 0L) friends.size shouldBe 1
        else friends.size shouldBe 0
    }

    test("모든 친구 요청 목록 조회") {
        val friendRequests = friendFindDao.findAllByInviteeIdAndLastThreeMonths(inviteeId)
        friendRequests.size shouldBe 1
    }
})

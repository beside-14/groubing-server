package com.beside.groubing.domain.friend.dao

import com.beside.groubing.aMember
import com.beside.groubing.config.QuerydslConfig
import com.beside.groubing.domain.friend.domain.Friend
import com.beside.groubing.domain.friend.domain.FriendStatus
import com.beside.groubing.domain.friend.entity.FriendEntity
import com.beside.groubing.domain.friend.repository.FriendJpaRepository
import com.beside.groubing.domain.member.repository.MemberJpaRepository
import com.beside.groubing.persistence.LocalPersistenceTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@LocalPersistenceTest
@Import(QuerydslConfig::class, FriendFindDao::class)
class FriendFindDaoTest(
    private val friendFindDao: FriendFindDao,
    private val friendJpaRepository: FriendJpaRepository,
    private val memberRepository: MemberJpaRepository
) : FunSpec({

    var memberId: Long = 0L

    beforeEach {
        memberRepository.saveAll((1L..100L).map { aMember(it) })
        val members = memberRepository.findAll()
        members.sortBy { member -> member.id }

        friendJpaRepository.saveAll(
            (0..<members.size - 1).map { i ->
                val entity = FriendEntity(inviterId = members[i].id, inviteeId = members[i + 1].id)
                if (members[i + 1].id % 2 == 0L) {
                    entity.applyStatus(Friend.of(0L, members[i].id, members[i + 1].id, FriendStatus.ACCEPT))
                }
                entity
            }
        )
        val friends = friendJpaRepository.findAll()
        friends.sortBy { friend -> friend.id }

        memberId = friends.filter { friend -> friend.status.isPending() }.random().inviteeId
    }

    test("수락한 친구 목록 조회") {
        val friends = friendFindDao.findAllAcceptedOf(memberId)
        friends.size shouldBe 1
    }

    test("모든 친구 요청 목록 조회") {
        val friendRequests = friendFindDao.findAllReceivedBy(memberId)
        friendRequests.size shouldBe 1
    }
})

package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.aMember
import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberValidateDao
import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.dao.FriendValidateDao
import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.justRun
import io.mockk.spyk

class FriendAddServiceTest(
    @MockkBean private val blockedMemberValidateDao: BlockedMemberValidateDao,
    @MockkBean private val friendValidateDao: FriendValidateDao,
    @MockkBean private val friendFindDao: FriendFindDao,
    @MockkBean private val memberFindDao: MemberFindDao,
    @MockkBean private val friendRepository: FriendRepository
) : BehaviorSpec({

    Given("친구 요청 시") {
        val friendAddService = spyk(
            FriendAddService(
                blockedMemberValidateDao,
                friendValidateDao,
                friendFindDao,
                memberFindDao,
                friendRepository
            )
        )
        val inviterId = Arb.long(1L..100L).single()
        val inviteeId = Arb.long(1L..100L).single()

        When("수락하거나 대기 상태의 데이터가 존재하는 경우") {
            justRun { blockedMemberValidateDao.validateEachOther(any(), any()) }
            every { friendFindDao.findByFriends(any(), any()) } returns listOf(
                Friend.create(
                    aMember(inviterId),
                    aMember(inviteeId)
                )
            )
            every { friendValidateDao.validateAddFriend(any()) } throws FriendInputException("이미 친구이거나 친구 수락 대기 상태입니다.")

            Then("예외를 발생한다.") {
                shouldThrow<FriendInputException> { friendAddService.add(inviterId, inviteeId) }
            }
        }
    }
})

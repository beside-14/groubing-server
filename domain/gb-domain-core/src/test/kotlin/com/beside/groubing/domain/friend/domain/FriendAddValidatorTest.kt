package com.beside.groubing.domain.friend.domain

import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.domain.blockedmember.exception.BlockedMemberInputException
import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.member.exception.MemberInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

class FriendAddValidatorTest : BehaviorSpec({
    val blockedMemberRepository = mockk<BlockedMemberRepository>()
    val memberQueryRepository = mockk<MemberQueryRepository>()
    val validator = FriendAddValidator(blockedMemberRepository, memberQueryRepository)

    val inviterId = 1L
    val inviteeId = 2L

    Given("차단 관계가 없고 두 회원 모두 존재할 때") {
        every { blockedMemberRepository.exists(inviterId, inviteeId) } returns false
        every { blockedMemberRepository.exists(inviteeId, inviterId) } returns false
        every { memberQueryRepository.findActiveById(inviterId) } returns mockk<Member>()
        every { memberQueryRepository.findActiveById(inviteeId) } returns mockk<Member>()

        When("validate 호출") {
            Then("예외가 발생하지 않는다") {
                validator.validate(inviterId, inviteeId)
            }
        }
    }

    Given("내가 상대방을 차단한 경우") {
        every { blockedMemberRepository.exists(inviterId, inviteeId) } returns true
        every { blockedMemberRepository.exists(inviteeId, inviterId) } returns false

        When("validate 호출") {
            Then("BlockedMemberInputException 발생") {
                shouldThrow<BlockedMemberInputException> { validator.validate(inviterId, inviteeId) }
            }
        }
    }

    Given("상대방이 나를 차단한 경우") {
        every { blockedMemberRepository.exists(inviterId, inviteeId) } returns false
        every { blockedMemberRepository.exists(inviteeId, inviterId) } returns true

        When("validate 호출") {
            Then("BlockedMemberInputException 발생") {
                shouldThrow<BlockedMemberInputException> { validator.validate(inviterId, inviteeId) }
            }
        }
    }

    Given("상대방이 존재하지 않는 회원인 경우") {
        every { blockedMemberRepository.exists(any(), any()) } returns false
        every { memberQueryRepository.findActiveById(inviterId) } returns mockk<Member>()
        every { memberQueryRepository.findActiveById(inviteeId) } throws MemberInputException("회원이 존재하지 않습니다.")

        When("validate 호출") {
            Then("MemberInputException 이 전파된다") {
                shouldThrow<MemberInputException> { validator.validate(inviterId, inviteeId) }
            }
        }
    }
})

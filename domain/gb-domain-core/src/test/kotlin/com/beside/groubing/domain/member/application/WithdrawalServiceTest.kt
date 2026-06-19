package com.beside.groubing.domain.member.application

import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.member.exception.MemberInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class WithdrawalServiceTest : BehaviorSpec({
    val mockMemberQueryRepository = mockk<MemberQueryRepository>()
    val mockMemberCommandRepository = mockk<MemberCommandRepository>()
    val withdrawalService = WithdrawalService(mockMemberQueryRepository, mockMemberCommandRepository)

    fun aMember(active: Boolean) = Member(
        id = 1L,
        loginId = "groubing",
        password = "encoded",
        nickname = "nickname",
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC,
        fcmToken = null,
        notificationReceive = true,
        active = active,
        deletedAt = null,
        profileUrl = null
    )

    Given("활성 회원이 탈퇴를 요청했을 때") {
        val memberId = 1L
        every { mockMemberQueryRepository.findById(memberId) } returns aMember(active = true)
        every { mockMemberCommandRepository.withdraw(memberId, any<LocalDateTime>()) } just Runs

        When("withdrawal 을 호출하면") {
            withdrawalService.withdrawal(memberId)

            Then("해당 회원의 soft-delete(withdraw) 가 호출된다") {
                verify(exactly = 1) { mockMemberCommandRepository.withdraw(memberId, any<LocalDateTime>()) }
            }
        }
    }

    Given("이미 탈퇴한 회원이 탈퇴를 다시 요청했을 때") {
        val memberId = 1L
        every { mockMemberQueryRepository.findById(memberId) } returns aMember(active = false)

        When("withdrawal 을 호출하면") {
            Then("이미 탈퇴한 회원 예외가 발생하고 withdraw 는 호출되지 않는다") {
                shouldThrow<MemberInputException> {
                    withdrawalService.withdrawal(memberId)
                }
                verify(exactly = 0) { mockMemberCommandRepository.withdraw(any(), any()) }
            }
        }
    }
})

package com.beside.groubing.domain.withdrawal.application

import com.beside.groubing.aEmptyBingo
import com.beside.groubing.aEnglishStudyBingoBoard
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.notification.domain.port.NotificationRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class WithdrawnMemberBingoCleanerTest : BehaviorSpec({
    val bingoBoardQueryRepository = mockk<BingoBoardQueryRepository>()
    val bingoBoardCommandRepository = mockk<BingoBoardCommandRepository>(relaxed = true)
    val notificationRepository = mockk<NotificationRepository>(relaxed = true)
    val memberQueryRepository = mockk<MemberQueryRepository>()
    val cleaner = WithdrawnMemberBingoCleaner(
        bingoBoardQueryRepository,
        bingoBoardCommandRepository,
        notificationRepository,
        memberQueryRepository
    )

    fun activeMember(memberId: Long): Member = mockk { every { id } returns memberId }

    // aEmptyBingo: id=1L, 멤버는 리더(memberId=1) 단독 / aEnglishStudyBingoBoard: id=2L, 리더 1 + 참여자 2,3,7
    val withdrawnMemberId = 1L

    Given("탈퇴 회원이 단독 보드만 가질 때") {
        every { bingoBoardQueryRepository.findAllOf(withdrawnMemberId) } returns listOf(aEmptyBingo())

        When("보드를 정리하면") {
            val remainsInGroupBingo = cleaner.cleanUpBoardsOf(withdrawnMemberId)

            Then("보드와 보드 알림을 hard-delete 하고 그룹 잔존이 아니다") {
                remainsInGroupBingo shouldBe false
                verify(exactly = 1) { notificationRepository.deleteAllByBingoBoardId(1L) }
                verify(exactly = 1) { bingoBoardCommandRepository.hardDelete(1L) }
                verify(exactly = 0) { bingoBoardCommandRepository.changeLeader(any(), any()) }
            }
        }
    }

    Given("탈퇴 회원이 리더인 그룹 보드를 가질 때") {
        every { bingoBoardQueryRepository.findAllOf(withdrawnMemberId) } returns listOf(aEnglishStudyBingoBoard())
        every { memberQueryRepository.findAllActive(listOf(2L, 3L, 7L)) } returns
            listOf(activeMember(3L), activeMember(7L))

        When("보드를 정리하면") {
            val remainsInGroupBingo = cleaner.cleanUpBoardsOf(withdrawnMemberId)

            Then("살아있는 동료 중 최소 id 에게 리더를 이양하고 그룹에 잔존한다") {
                remainsInGroupBingo shouldBe true
                verify(exactly = 1) { bingoBoardCommandRepository.changeLeader(2L, 3L) }
                verify(exactly = 0) { bingoBoardCommandRepository.hardDelete(any()) }
            }
        }
    }

    Given("탈퇴 회원이 리더가 아닌 그룹 보드를 가질 때") {
        val participantId = 2L
        every { bingoBoardQueryRepository.findAllOf(participantId) } returns listOf(aEnglishStudyBingoBoard())
        every { memberQueryRepository.findAllActive(listOf(1L, 3L, 7L)) } returns
            listOf(activeMember(1L), activeMember(3L), activeMember(7L))

        When("보드를 정리하면") {
            val remainsInGroupBingo = cleaner.cleanUpBoardsOf(participantId)

            Then("리더 이양 없이 그룹에 잔존한다") {
                remainsInGroupBingo shouldBe true
                verify(exactly = 0) { bingoBoardCommandRepository.changeLeader(any(), any()) }
                verify(exactly = 0) { bingoBoardCommandRepository.hardDelete(any()) }
            }
        }
    }

    Given("그룹 보드의 동료가 전원 탈퇴(활성 계정 없음)일 때") {
        every { bingoBoardQueryRepository.findAllOf(withdrawnMemberId) } returns listOf(aEnglishStudyBingoBoard())
        every { memberQueryRepository.findAllActive(listOf(2L, 3L, 7L)) } returns emptyList()

        When("보드를 정리하면") {
            val remainsInGroupBingo = cleaner.cleanUpBoardsOf(withdrawnMemberId)

            Then("살아있는 사람이 없으므로 보드를 hard-delete 하고 그룹 잔존이 아니다") {
                remainsInGroupBingo shouldBe false
                verify(exactly = 1) { bingoBoardCommandRepository.hardDelete(2L) }
                verify(exactly = 0) { bingoBoardCommandRepository.changeLeader(any(), any()) }
            }
        }
    }

    Given("탈퇴 회원이 단독 보드와 리더인 그룹 보드를 함께 가질 때") {
        every { bingoBoardQueryRepository.findAllOf(withdrawnMemberId) } returns
            listOf(aEmptyBingo(), aEnglishStudyBingoBoard())
        every { memberQueryRepository.findAllActive(listOf(2L, 3L, 7L)) } returns
            listOf(activeMember(3L), activeMember(7L))

        When("보드를 정리하면") {
            val remainsInGroupBingo = cleaner.cleanUpBoardsOf(withdrawnMemberId)

            Then("단독 보드는 삭제하고 그룹 보드는 이양하며 그룹에 잔존한다") {
                remainsInGroupBingo shouldBe true
                verify(exactly = 1) { bingoBoardCommandRepository.hardDelete(1L) }
                verify(exactly = 1) { bingoBoardCommandRepository.changeLeader(2L, 3L) }
            }
        }
    }
})

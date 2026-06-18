package com.beside.groubing.domain.bingo.application

import com.beside.groubing.aEnglishStudyBingoBoard
import com.beside.groubing.domain.bingo.domain.BingoBoardMembersValidator
import com.beside.groubing.domain.bingo.domain.BingoMember
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.bingo.payload.command.BingoBoardBaseUpdateCommand
import com.beside.groubing.domain.bingo.payload.command.BingoBoardMembersPeriodUpdateCommand
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.LocalDate

class BingoBoardUpdateServiceTest : BehaviorSpec({
    val bingoBoardQueryRepository = mockk<BingoBoardQueryRepository>()
    val bingoBoardCommandRepository = mockk<BingoBoardCommandRepository>()
    val bingoBoardMembersValidator = mockk<BingoBoardMembersValidator>()
    val bingoBoardUpdateService = BingoBoardUpdateService(
        bingoBoardQueryRepository,
        bingoBoardCommandRepository,
        bingoBoardMembersValidator
    )

    given("리더(memberId=1)가 기본정보 변경을 요청할 때") {
        val leaderId = 1L
        val bingoBoardId = 2L
        val newTitle = "새 제목"
        val newGoal = 5
        val since = LocalDate.now()
        val until = LocalDate.now().plusDays(7)
        val command = BingoBoardBaseUpdateCommand.of(newTitle, newGoal, since, until)
        val bingoBoard = aEnglishStudyBingoBoard()

        every { bingoBoardQueryRepository.findOne(bingoBoardId) } returns bingoBoard
        justRun { bingoBoardCommandRepository.updateBase(bingoBoardId, any(), any(), any()) }

        `when`("updateBase 를 호출하면") {
            val result = bingoBoardUpdateService.updateBase(bingoBoardId, leaderId, command)

            then("타깃 영속화 경로(updateBase)를 타고 스냅샷 update 는 호출하지 않는다") {
                verify(exactly = 1) { bingoBoardCommandRepository.updateBase(bingoBoardId, newTitle, any(), any()) }
                verify(exactly = 0) { bingoBoardCommandRepository.update(any()) }
            }

            then("기본정보가 변경된 도메인을 그대로 반환한다") {
                result.title shouldBe newTitle
                result.goal shouldBe newGoal
                result.since shouldBe since
                result.until shouldBe until
            }
        }
    }

    given("리더(memberId=1)가 멤버/기간 변경(발행)을 요청할 때") {
        val leaderId = 1L
        val bingoBoardId = 2L
        val newMemberIds = listOf(4L, 5L)
        val since = LocalDate.now()
        val until = LocalDate.now().plusDays(7)
        val command = BingoBoardMembersPeriodUpdateCommand.of(newMemberIds, since, until)
        val bingoBoard = aEnglishStudyBingoBoard()
        val addedMembersSlot = slot<List<BingoMember>>()

        justRun { bingoBoardMembersValidator.validate(newMemberIds) }
        every { bingoBoardQueryRepository.findOne(bingoBoardId) } returns bingoBoard
        justRun { bingoBoardCommandRepository.addBingoMembers(bingoBoardId, capture(addedMembersSlot)) }
        justRun { bingoBoardCommandRepository.updatePeriod(bingoBoardId, any()) }

        `when`("updateMembersPeriod 를 호출하면") {
            val result = bingoBoardUpdateService.updateMembersPeriod(bingoBoardId, leaderId, command)

            then("멤버 검증 후 멤버 INSERT(addBingoMembers)·기간 UPDATE(updatePeriod) 타깃 경로를 타고 스냅샷 update 는 호출하지 않는다") {
                verify(exactly = 1) { bingoBoardMembersValidator.validate(newMemberIds) }
                verify(exactly = 1) { bingoBoardCommandRepository.addBingoMembers(bingoBoardId, any()) }
                verify(exactly = 1) { bingoBoardCommandRepository.updatePeriod(bingoBoardId, any()) }
                verify(exactly = 0) { bingoBoardCommandRepository.update(any()) }
            }

            then("새로 추가된 멤버만 영속화 경로로 전달한다") {
                addedMembersSlot.captured.map { it.memberId } shouldBe newMemberIds
            }

            then("멤버목록·기간이 반영된 도메인을 그대로 반환한다") {
                result.bingoMembers.memberIds().toSet() shouldBe setOf(1L, 2L, 3L, 7L, 4L, 5L)
                result.since shouldBe since
                result.until shouldBe until
            }
        }
    }

    given("리더(memberId=1)가 메모 변경을 요청할 때") {
        val leaderId = 1L
        val bingoBoardId = 2L
        val newMemo = "메모"
        val bingoBoard = aEnglishStudyBingoBoard()

        every { bingoBoardQueryRepository.findOne(bingoBoardId) } returns bingoBoard
        justRun { bingoBoardCommandRepository.updateMemo(bingoBoardId, newMemo) }

        `when`("updateMemo 를 호출하면") {
            val result = bingoBoardUpdateService.updateMemo(bingoBoardId, leaderId, newMemo)

            then("타깃 영속화 경로(updateMemo)를 타고 스냅샷 update 는 호출하지 않는다") {
                verify(exactly = 1) { bingoBoardCommandRepository.updateMemo(bingoBoardId, newMemo) }
                verify(exactly = 0) { bingoBoardCommandRepository.update(any()) }
            }

            then("메모가 변경된 도메인을 그대로 반환한다") {
                result.memo shouldBe newMemo
            }
        }
    }

    given("리더(memberId=1)가 공개여부 변경을 요청할 때") {
        val leaderId = 1L
        val bingoBoardId = 2L
        val newOpen = false
        val bingoBoard = aEnglishStudyBingoBoard()

        every { bingoBoardQueryRepository.findOne(bingoBoardId) } returns bingoBoard
        justRun { bingoBoardCommandRepository.updateOpen(bingoBoardId, newOpen) }

        `when`("updateOpen 을 호출하면") {
            val result = bingoBoardUpdateService.updateOpen(bingoBoardId, leaderId, newOpen)

            then("타깃 영속화 경로(updateOpen)를 타고 스냅샷 update 는 호출하지 않는다") {
                verify(exactly = 1) { bingoBoardCommandRepository.updateOpen(bingoBoardId, newOpen) }
                verify(exactly = 0) { bingoBoardCommandRepository.update(any()) }
            }

            then("공개여부가 변경된 도메인을 그대로 반환한다") {
                result.open shouldBe newOpen
            }
        }
    }
})

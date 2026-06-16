package com.beside.groubing.domain.bingo.application

import com.beside.groubing.aEnglishStudyBingoBoard
import com.beside.groubing.domain.bingo.domain.BingoBoardMembersValidator
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.bingo.payload.command.BingoBoardMemoUpdateCommand
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class BingoBoardUpdateServiceTest : BehaviorSpec({
    val bingoBoardQueryRepository = mockk<BingoBoardQueryRepository>()
    val bingoBoardCommandRepository = mockk<BingoBoardCommandRepository>()
    val bingoBoardMembersValidator = mockk<BingoBoardMembersValidator>()
    val bingoBoardUpdateService = BingoBoardUpdateService(
        bingoBoardQueryRepository,
        bingoBoardCommandRepository,
        bingoBoardMembersValidator
    )

    given("리더(memberId=1)가 메모 변경을 요청할 때") {
        val leaderId = 1L
        val bingoBoardId = 2L
        val newMemo = "메모"
        val bingoBoard = aEnglishStudyBingoBoard()
        val command = BingoBoardMemoUpdateCommand.of(newMemo)

        every { bingoBoardQueryRepository.findOne(bingoBoardId) } returns bingoBoard
        justRun { bingoBoardCommandRepository.updateMemo(bingoBoardId, newMemo) }

        `when`("updateMemo 를 호출하면") {
            val result = bingoBoardUpdateService.updateMemo(bingoBoardId, leaderId, command)

            then("타깃 영속화 경로(updateMemo)를 타고 스냅샷 update 는 호출하지 않는다") {
                verify(exactly = 1) { bingoBoardCommandRepository.updateMemo(bingoBoardId, newMemo) }
                verify(exactly = 0) { bingoBoardCommandRepository.update(any()) }
            }

            then("메모가 변경된 도메인을 그대로 반환한다") {
                result.memo shouldBe newMemo
            }
        }
    }
})

package com.beside.groubing.domain.bingo.application

import com.beside.groubing.aEnglishStudyBingoBoard
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.bingo.exception.BingoIllegalStateException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class BingoMemberLeaveServiceTest : BehaviorSpec({
    val bingoBoardQueryRepository = mockk<BingoBoardQueryRepository>()
    val bingoBoardCommandRepository = mockk<BingoBoardCommandRepository>()
    val bingoMemberLeaveService = BingoMemberLeaveService(
        bingoBoardQueryRepository,
        bingoBoardCommandRepository
    )

    given("참여자(memberId=2)가 빙고 나가기를 요청할 때") {
        val participantId = 2L
        val bingoBoardId = 2L
        val bingoBoard = aEnglishStudyBingoBoard()

        every { bingoBoardQueryRepository.findOne(bingoBoardId) } returns bingoBoard
        justRun { bingoBoardCommandRepository.deactivateBingoMember(bingoBoardId, participantId) }

        `when`("leave 를 호출하면") {
            bingoMemberLeaveService.leave(participantId, bingoBoardId)

            then("타깃 비활성화 경로(deactivateBingoMember)를 타고 스냅샷 update 는 호출하지 않는다") {
                verify(exactly = 1) { bingoBoardCommandRepository.deactivateBingoMember(bingoBoardId, participantId) }
                verify(exactly = 0) { bingoBoardCommandRepository.update(any()) }
            }
        }
    }

    given("리더(memberId=1)가 빙고 나가기를 요청할 때") {
        val leaderId = 1L
        val bingoBoardId = 2L
        val bingoBoard = aEnglishStudyBingoBoard()

        every { bingoBoardQueryRepository.findOne(bingoBoardId) } returns bingoBoard

        `when`("leave 를 호출하면") {
            then("리더는 나갈 수 없어 예외가 발생하고 비활성화는 호출되지 않는다") {
                shouldThrow<BingoIllegalStateException> {
                    bingoMemberLeaveService.leave(leaderId, bingoBoardId)
                }
                verify(exactly = 0) { bingoBoardCommandRepository.deactivateBingoMember(any(), any()) }
            }
        }
    }
})

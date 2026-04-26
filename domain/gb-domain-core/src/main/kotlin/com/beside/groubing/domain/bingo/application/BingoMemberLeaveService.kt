package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoMemberLeaveService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository
) {
    fun leave(memberId: Long, bingoId: Long) {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoId)
        bingoBoard.validateCanLeave(memberId)
        bingoBoard.inactiveByMemberId(memberId)
        bingoBoardCommandRepository.update(bingoBoard)
    }
}

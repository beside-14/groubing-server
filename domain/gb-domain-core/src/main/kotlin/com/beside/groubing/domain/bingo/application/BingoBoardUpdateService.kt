package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.BingoBoardMembersValidator
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.bingo.payload.command.BingoBoardBaseUpdateCommand
import com.beside.groubing.domain.bingo.payload.command.BingoBoardMembersPeriodUpdateCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoBoardUpdateService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository,
    private val bingoBoardMembersValidator: BingoBoardMembersValidator
) {
    fun updateBase(bingoBoardId: Long, memberId: Long, command: BingoBoardBaseUpdateCommand): BingoBoard {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        command.update(bingoBoard, memberId)
        bingoBoardCommandRepository.updateBase(bingoBoardId, bingoBoard.title, bingoBoard.bingoGoal, bingoBoard.period!!)
        return bingoBoard
    }

    fun updateMembersPeriod(bingoBoardId: Long, memberId: Long, command: BingoBoardMembersPeriodUpdateCommand): BingoBoard {
        bingoBoardMembersValidator.validate(command.bingoMembers)
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        command.update(bingoBoard, memberId)
        return bingoBoardCommandRepository.update(bingoBoard)
    }

    fun updateMemo(bingoBoardId: Long, memberId: Long, memo: String?): BingoBoard {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        bingoBoard.updateBingoMemo(memberId, memo)
        bingoBoardCommandRepository.updateMemo(bingoBoardId, bingoBoard.memo)
        return bingoBoard
    }

    fun updateOpen(bingoBoardId: Long, memberId: Long, open: Boolean): BingoBoard {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        bingoBoard.updateBingoOpen(memberId, open)
        bingoBoardCommandRepository.updateOpen(bingoBoardId, bingoBoard.open)
        return bingoBoard
    }
}

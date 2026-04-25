package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.BingoBoardMembersValidator
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.bingo.payload.command.BingoBoardBaseUpdateCommand
import com.beside.groubing.domain.bingo.payload.command.BingoBoardMembersPeriodUpdateCommand
import com.beside.groubing.domain.bingo.payload.command.BingoBoardMemoUpdateCommand
import com.beside.groubing.domain.bingo.payload.command.BingoBoardOpenUpdateCommand
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
        return bingoBoardCommandRepository.update(bingoBoard)
    }

    fun updateMembersPeriod(bingoBoardId: Long, memberId: Long, command: BingoBoardMembersPeriodUpdateCommand): BingoBoard {
        bingoBoardMembersValidator.validate(command.bingoMembers)
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        command.update(bingoBoard, memberId)
        return bingoBoardCommandRepository.update(bingoBoard)
    }

    fun updateMemo(bingoBoardId: Long, memberId: Long, command: BingoBoardMemoUpdateCommand): BingoBoard {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        command.update(bingoBoard, memberId)
        return bingoBoardCommandRepository.update(bingoBoard)
    }

    fun updateOpen(bingoBoardId: Long, memberId: Long, command: BingoBoardOpenUpdateCommand): BingoBoard {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        command.update(bingoBoard, memberId)
        return bingoBoardCommandRepository.update(bingoBoard)
    }
}

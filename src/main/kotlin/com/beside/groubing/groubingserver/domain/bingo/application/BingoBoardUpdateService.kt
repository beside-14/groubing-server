package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardBaseUpdateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardMembersPeriodUpdateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardMemoUpdateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardOpenUpdateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoBoardUpdateService(
    private val bingoBoardFindDao: BingoBoardFindDao
) {
    fun updateBase(bingoBoardId: Long, memberId: Long, baseUpdateCommand: BingoBoardBaseUpdateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        baseUpdateCommand.update(bingoBoard, memberId)
        return BingoBoardResponse.fromBingoBoard(bingoBoard, memberId)
    }

    fun updateMembersPeriod(bingoBoardId: Long, memberId: Long, membersPeriodUpdateCommand: BingoBoardMembersPeriodUpdateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        membersPeriodUpdateCommand.update(bingoBoard, memberId)
        return BingoBoardResponse.fromBingoBoard(bingoBoard, memberId)
    }

    fun updateMemo(bingoBoardId: Long, memberId: Long, memoUpdateCommand: BingoBoardMemoUpdateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        memoUpdateCommand.update(bingoBoard, memberId)
        return BingoBoardResponse.fromBingoBoard(bingoBoard, memberId)
    }

    fun updateOpen(bingoBoardId: Long, memberId: Long, openUpdateCommand: BingoBoardOpenUpdateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        openUpdateCommand.update(bingoBoard, memberId)
        return BingoBoardResponse.fromBingoBoard(bingoBoard, memberId)
    }
}

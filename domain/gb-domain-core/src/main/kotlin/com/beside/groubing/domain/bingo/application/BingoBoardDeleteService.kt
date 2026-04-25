package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoBoardDeleteService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository
) {
    fun deleteBingoBoard(memberId: Long, boardId: Long) {
        val bingoBoard = bingoBoardQueryRepository.findOne(boardId)
        bingoBoard.validateAuthority(memberId)
        bingoBoard.delete()
        bingoBoardCommandRepository.update(bingoBoard)
    }
}

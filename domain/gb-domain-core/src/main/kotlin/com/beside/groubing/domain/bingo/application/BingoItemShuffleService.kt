package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.map.BingoMap
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoItemShuffleService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository
) {
    fun shuffleBingoItems(memberId: Long, boardId: Long): BingoMap {
        val bingoBoard = bingoBoardQueryRepository.findOne(boardId)
        bingoBoard.shuffleBingoItems()
        val updated = bingoBoardCommandRepository.update(bingoBoard)
        return updated.makeBingoMap(memberId)
    }
}

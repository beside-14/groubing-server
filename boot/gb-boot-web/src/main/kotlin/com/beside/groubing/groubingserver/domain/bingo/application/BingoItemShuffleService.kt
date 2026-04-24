package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction
import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoLineResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoItemShuffleService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository
) {
    fun shuffleBingoItems(memberId: Long, boardId: Long): List<BingoLineResponse> {
        val bingoBoard = bingoBoardQueryRepository.findOne(boardId)
        bingoBoard.shuffleBingoItems()
        val updated = bingoBoardCommandRepository.update(bingoBoard)
        return updated.makeBingoMap(memberId).getBingoLines(Direction.HORIZONTAL)
            .map { BingoLineResponse.fromBingoLine(it, memberId) }
    }
}

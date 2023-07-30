package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoLineResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoItemShuffleService (
    private val bingoBoardFindDao: BingoBoardFindDao
) {
    fun shuffleBingoItems(memberId: Long, boardId: Long): List<BingoLineResponse> {
        val bingoBoard = bingoBoardFindDao.findById(boardId)
        bingoBoard.shuffleBingoItems()
        val bingoMap = bingoBoard.makeBingoMap(memberId)
        return bingoMap.getBingoLines(Direction.HORIZONTAL)
            .map { BingoLineResponse.fromBingoLine(it, memberId) }
    }
}

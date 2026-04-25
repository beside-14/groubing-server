package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoItemCompleteService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository
) {
    fun completeBingoItem(bingoBoardId: Long, bingoItemId: Long, memberId: Long): BingoMap {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        bingoBoard.completeBingoItem(bingoItemId = bingoItemId, memberId = memberId)
        val updated = bingoBoardCommandRepository.update(bingoBoard)
        return updated.makeBingoMap(memberId)
    }

    fun cancelBingoItem(bingoBoardId: Long, bingoItemId: Long, memberId: Long): BingoMap {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        bingoBoard.cancelBingoItem(bingoItemId = bingoItemId, memberId = memberId)
        val updated = bingoBoardCommandRepository.update(bingoBoard)
        return updated.makeBingoMap(memberId)
    }
}

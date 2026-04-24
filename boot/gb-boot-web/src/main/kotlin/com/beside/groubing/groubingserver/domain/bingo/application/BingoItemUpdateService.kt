package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoItemUpdateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoItemResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoItemUpdateService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository
) {
    fun updateBingoItem(bingoBoardId: Long, bingoItemId: Long, memberId: Long, command: BingoItemUpdateCommand): BingoItemResponse {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        bingoBoard.updateBingoItem(memberId, bingoItemId, command.title, command.subTitle)
        val updated = bingoBoardCommandRepository.update(bingoBoard)
        val updatedItem = updated.bingoItems.first { it.id == bingoItemId }
        return BingoItemResponse.fromBingoItem(updatedItem, memberId)
    }
}

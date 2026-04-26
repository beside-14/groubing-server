package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.BingoItem
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.bingo.payload.command.BingoItemUpdateCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoItemUpdateService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository
) {
    fun update(bingoBoardId: Long, bingoItemId: Long, memberId: Long, command: BingoItemUpdateCommand): BingoItem {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        bingoBoard.updateBingoItem(memberId, bingoItemId, command.title, command.subTitle)
        val updated = bingoBoardCommandRepository.update(bingoBoard)
        return updated.bingoItems.first { it.id == bingoItemId }
    }
}

package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoItemUpdateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoItemResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoItemUpdateService(
    private val bingoBoardFindDao: BingoBoardFindDao
) {
    fun updateBingoItem(bingoBoardId: Long, bingoItemId: Long, memberId: Long, bingoItemUpdateCommand: BingoItemUpdateCommand): BingoItemResponse {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        val updateBingoItem = bingoBoard.updateBingoItem(memberId, bingoItemId, bingoItemUpdateCommand)
        return BingoItemResponse.fromBingoItem(updateBingoItem, memberId)
    }
}

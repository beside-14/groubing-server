package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoItemCompleteService(
    private val bingoBoardFindDao: BingoBoardFindDao
) {
    fun completeBingoItem(bingoBoardId: Long, bingoItemId: Long, memberId: Long) {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        bingoBoard.completeBingoItem(memberId, bingoItemId)
    }

    fun cancelBingoItem(bingoBoardId: Long, bingoItemId: Long, memberId: Long) {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        bingoBoard.cancelBingoItem(memberId, bingoItemId)
    }
}

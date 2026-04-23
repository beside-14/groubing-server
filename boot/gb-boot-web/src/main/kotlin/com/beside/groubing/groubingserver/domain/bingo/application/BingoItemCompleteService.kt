package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoCalculatingResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoItemCompleteService(
    private val bingoBoardFindDao: BingoBoardFindDao,

    private val bingoBoardRepository: BingoBoardRepository
) {
    fun completeBingoItem(bingoBoardId: Long, bingoItemId: Long, memberId: Long): BingoCalculatingResponse {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        bingoBoard.completeBingoItem(memberId = memberId, bingoItemId = bingoItemId)
        bingoBoardRepository.save(bingoBoard)
        return BingoCalculatingResponse.fromBingoMap(bingoBoard.makeBingoMap(memberId))
    }

    fun cancelBingoItem(bingoBoardId: Long, bingoItemId: Long, memberId: Long): BingoCalculatingResponse {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        bingoBoard.cancelBingoItem(memberId = memberId, bingoItemId = bingoItemId)
        bingoBoardRepository.save(bingoBoard)
        return BingoCalculatingResponse.fromBingoMap(bingoBoard.makeBingoMap(memberId))
    }
}

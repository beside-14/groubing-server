package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoBoardDeleteService(
    private val bingoBoardFindDao: BingoBoardFindDao,
    private val bingoBoardRepository: BingoBoardRepository
) {
    fun deleteBingoBoard(memberId: Long, boardId: Long) {
        val bingoBoard = bingoBoardFindDao.findById(boardId)
        bingoBoard.validateAuthority(memberId)
        bingoBoardRepository.delete(bingoBoard)
    }
}

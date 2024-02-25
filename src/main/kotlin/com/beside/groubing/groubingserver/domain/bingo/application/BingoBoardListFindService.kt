package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardOverviewResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardListFindService(
    private val bingoBoardListFindDao: BingoBoardListFindDao
) {
    fun findBingoBoardList(memberId: Long, loginMemberId: Long): List<BingoBoardOverviewResponse> {
        var bingoBoards = bingoBoardListFindDao.findBingoBoardList(memberId)
        if (memberId != loginMemberId) {
            bingoBoards =
                bingoBoards.filter { bingoBoard -> bingoBoard.isStarted() }
        }
        return bingoBoards.map { BingoBoardOverviewResponse.fromBingoBoard(it, memberId) }
    }
}


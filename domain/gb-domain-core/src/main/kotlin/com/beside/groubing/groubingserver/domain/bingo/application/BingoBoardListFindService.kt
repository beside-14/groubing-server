package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardListFindService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository
) {
    fun findBingoBoardList(memberId: Long, loginMemberId: Long): List<BingoBoard> {
        val bingoBoards = bingoBoardQueryRepository.findAllOf(memberId)
        if (memberId == loginMemberId) {
            return bingoBoards
        }
        return bingoBoards.filter { it.isStarted() }
    }
}

package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardOverviewResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardListFindService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository
) {
    fun findBingoBoardList(memberId: Long, loginMemberId: Long): List<BingoBoardOverviewResponse> {
        var bingoBoards = bingoBoardQueryRepository.findAllOf(memberId)
        if (memberId != loginMemberId) {
            bingoBoards = bingoBoards.filter { it.isStarted() }
        }
        return bingoBoards.map { BingoBoardOverviewResponse.fromBingoBoard(it, memberId) }
    }
}

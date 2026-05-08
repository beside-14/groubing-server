package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.BingoBoards
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardListFindService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository
) {
    fun find(memberId: Long, loginMemberId: Long): List<BingoBoard> {
        val bingoBoards = BingoBoards(bingoBoardQueryRepository.findAllOf(memberId))
        return bingoBoards.visibleTo(viewerMemberId = loginMemberId, ownerMemberId = memberId)
    }
}

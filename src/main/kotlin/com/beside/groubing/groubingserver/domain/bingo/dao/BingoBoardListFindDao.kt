package com.beside.groubing.groubingserver.domain.bingo.dao

import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoBoard.bingoBoard
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardOverviewResponse
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BingoBoardListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findBingoBoardList(memberId: Long): List<BingoBoardOverviewResponse> {
        val bingoBoards = queryFactory.select(bingoBoard)
            .from(bingoBoard)
            .where(bingoBoard.bingoMembers.any().memberId.eq(memberId))
            .orderBy(bingoBoard.lastModifiedDate.desc())
            .fetch()
        return bingoBoards.map { BingoBoardOverviewResponse.fromBingoBoard(it, memberId) }
    }
}

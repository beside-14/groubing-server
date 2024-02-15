package com.beside.groubing.groubingserver.domain.bingo.dao

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoBoard.bingoBoard
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BingoBoardListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findBingoBoardList(memberId: Long): List<BingoBoard> {
        return queryFactory.select(bingoBoard)
            .from(bingoBoard)
            .where(
                bingoBoard.bingoMembers
                    .any().memberId.eq(memberId)
                    .and(bingoBoard.active.isTrue)
            )
            .orderBy(bingoBoard.lastModifiedDate.desc())
            .fetch()
    }
}

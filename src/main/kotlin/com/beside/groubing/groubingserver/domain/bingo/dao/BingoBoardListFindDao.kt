package com.beside.groubing.groubingserver.domain.bingo.dao

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoBoard.bingoBoard
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BingoBoardListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findBingoBoardList(memberId: Long): List<BingoBoard> {
        val qBingoBoard = bingoBoard
        val qBingoMember = qBingoBoard.bingoMembers.any()
        val predicate =
            BooleanBuilder().and(qBingoMember.memberId.eq(memberId).and(qBingoMember.active.isTrue))
                .and(qBingoBoard.active.isTrue)
                .value
        return queryFactory.selectFrom(qBingoBoard)
            .where(predicate)
            .orderBy(qBingoBoard.lastModifiedDate.desc())
            .fetch()
    }
}

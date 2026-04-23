package com.beside.groubing.groubingserver.domain.bingo.dao

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoBoard.bingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoMember.bingoMember
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BingoBoardListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findBingoBoardList(memberId: Long): List<BingoBoard> {
        val qBingoBoBoard = bingoBoard
        val qBingoMember = bingoMember
        val predicate = BooleanBuilder()
            .and(
                qBingoBoBoard.active.isTrue
                    .and(qBingoMember.active.isTrue)
                    .and(qBingoMember.memberId.eq(memberId))
            ).value

        return queryFactory.selectFrom(qBingoBoBoard)
            .innerJoin(qBingoBoBoard.bingoMembers, qBingoMember)
            .where(predicate)
            .orderBy(qBingoBoBoard.lastModifiedDate.desc())
            .fetch()
    }
}

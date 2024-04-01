package com.beside.groubing.groubingserver.domain.bingo.dao

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoBoard.bingoBoard
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BingoBoardListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findBingoBoardList(memberId: Long): List<BingoBoard> {
        val isMemberId = bingoBoard.bingoMembers.any().memberId.eq(memberId)
        val isActiveBingoMember = bingoBoard.bingoMembers.any().active.isTrue
        return queryFactory.selectDistinct(bingoBoard)
            .from(bingoBoard)
            .where(bingoBoard.active.isTrue.and(Expressions.allOf(isMemberId, isActiveBingoMember)))
            .orderBy(bingoBoard.lastModifiedDate.desc())
            .fetch()
    }
}

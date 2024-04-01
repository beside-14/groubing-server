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
        val qBingoMember = bingoBoard.bingoMembers.any()
        val bingoMemberPredicate = BooleanBuilder()
            .and(qBingoMember.memberId.eq(memberId).and(qBingoMember.active.isTrue))
            .value
        return queryFactory.selectDistinct(bingoBoard)
            .from(bingoBoard)
            .where(bingoBoard.active.isTrue)
            .where(bingoMemberPredicate)
            .orderBy(bingoBoard.lastModifiedDate.desc())
            .fetch()
    }
}

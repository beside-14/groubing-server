package com.beside.groubing.groubingserver.domain.bingo.dao

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoBoard.bingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoMember.bingoMember
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BingoBoardListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findBingoBoardList(memberId: Long): List<BingoBoard> {
        return queryFactory.selectDistinct(bingoBoard)
            .from(bingoBoard)
            .innerJoin(bingoBoard.bingoMembers).fetchJoin()
            .where(bingoMember.memberId.eq(memberId).and(bingoMember.active.isTrue).and(bingoBoard.active.isTrue))
            .orderBy(bingoBoard.lastModifiedDate.desc())
            .fetch()
    }
}

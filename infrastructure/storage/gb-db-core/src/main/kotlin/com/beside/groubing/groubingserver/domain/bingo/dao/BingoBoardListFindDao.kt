package com.beside.groubing.groubingserver.domain.bingo.dao

import com.beside.groubing.groubingserver.domain.bingo.entity.BingoBoardEntity
import com.beside.groubing.groubingserver.domain.bingo.entity.QBingoBoardEntity.bingoBoardEntity
import com.beside.groubing.groubingserver.domain.bingo.entity.QBingoMemberEntity.bingoMemberEntity
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BingoBoardListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findBingoBoardList(memberId: Long): List<BingoBoardEntity> {
        val predicate = BooleanBuilder()
            .and(
                bingoBoardEntity.active.isTrue
                    .and(bingoMemberEntity.active.isTrue)
                    .and(bingoMemberEntity.memberId.eq(memberId))
            ).value

        return queryFactory.selectFrom(bingoBoardEntity)
            .innerJoin(bingoBoardEntity.bingoMembers, bingoMemberEntity)
            .where(predicate)
            .orderBy(bingoBoardEntity.lastModifiedDate.desc())
            .fetch()
    }
}

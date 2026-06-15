package com.beside.groubing.domain.bingo.dao

import com.beside.groubing.domain.bingo.domain.BingoMemberType
import com.beside.groubing.domain.bingo.entity.BingoBoardEntity
import com.beside.groubing.domain.bingo.entity.QBingoBoardEntity.bingoBoardEntity
import com.beside.groubing.domain.bingo.entity.QBingoMemberEntity.bingoMemberEntity
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BingoBoardListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun find(memberId: Long): List<BingoBoardEntity> {
        val predicate = activePredicate(memberId)

        return queryFactory.selectFrom(bingoBoardEntity)
            .innerJoin(bingoBoardEntity.bingoMembers, bingoMemberEntity)
            .where(predicate)
            .orderBy(bingoBoardEntity.lastModifiedDate.desc())
            .fetch()
    }

    fun findAllIdsOf(memberId: Long): List<Long> {
        return queryFactory.select(bingoBoardEntity.id)
            .from(bingoBoardEntity)
            .innerJoin(bingoBoardEntity.bingoMembers, bingoMemberEntity)
            .where(activePredicate(memberId))
            .fetch()
    }

    fun isLeaderOf(bingoBoardId: Long, memberId: Long): Boolean {
        return queryFactory.selectOne()
            .from(bingoBoardEntity)
            .innerJoin(bingoBoardEntity.bingoMembers, bingoMemberEntity)
            .where(
                bingoBoardEntity.id.eq(bingoBoardId)
                    .and(bingoBoardEntity.active.isTrue)
                    .and(bingoMemberEntity.active.isTrue)
                    .and(bingoMemberEntity.memberId.eq(memberId))
                    .and(bingoMemberEntity.bingoMemberType.eq(BingoMemberType.LEADER))
            )
            .fetchFirst() != null
    }

    private fun activePredicate(memberId: Long) = BooleanBuilder()
        .and(
            bingoBoardEntity.active.isTrue
                .and(bingoMemberEntity.active.isTrue)
                .and(bingoMemberEntity.memberId.eq(memberId))
        ).value
}

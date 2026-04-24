package com.beside.groubing.groubingserver.domain.feed.dao

import com.beside.groubing.groubingserver.domain.bingo.entity.QBingoCompleteMemberEntity.bingoCompleteMemberEntity
import com.beside.groubing.groubingserver.domain.bingo.entity.QBingoItemEntity.bingoItemEntity
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FeedListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findRecentCompleterMemberIds(memberIds: List<Long>, isFriend: Boolean): List<Long> {
        val filter = if (isFriend) inFilter(memberIds) else notInFilter(memberIds)
        return queryFactory.selectDistinct(bingoCompleteMemberEntity.memberId)
            .from(bingoCompleteMemberEntity)
            .where(filter)
            .limit(MAX_COMPLETERS)
            .fetch()
    }

    fun findCompletedFeedItems(memberIds: List<Long>): List<FeedItemInfo> {
        if (memberIds.isEmpty()) return emptyList()
        return queryFactory.select(
            QFeedItemInfo(bingoCompleteMemberEntity.memberId, bingoItemEntity.title)
        )
            .from(bingoItemEntity)
            .innerJoin(bingoItemEntity.completeMembers, bingoCompleteMemberEntity)
            .where(
                bingoCompleteMemberEntity.memberId.`in`(memberIds)
                    .and(bingoCompleteMemberEntity.active.isTrue)
                    .and(bingoItemEntity.title.isNotNull)
                    .and(bingoItemEntity.bingoBoard.active.isTrue)
                    .and(bingoItemEntity.bingoBoard.period.isNotNull)
                    .and(bingoItemEntity.bingoBoard.open.isTrue)
            )
            .fetch()
    }

    private fun inFilter(memberIds: List<Long>): BooleanExpression? =
        memberIds.takeIf { it.isNotEmpty() }
            ?.let { bingoCompleteMemberEntity.memberId.`in`(it).and(bingoCompleteMemberEntity.active.isTrue) }

    private fun notInFilter(memberIds: List<Long>): BooleanExpression? =
        memberIds.takeIf { it.isNotEmpty() }
            ?.let { bingoCompleteMemberEntity.memberId.notIn(it).and(bingoCompleteMemberEntity.active.isTrue) }

    companion object {
        private const val MAX_COMPLETERS = 20L
    }
}

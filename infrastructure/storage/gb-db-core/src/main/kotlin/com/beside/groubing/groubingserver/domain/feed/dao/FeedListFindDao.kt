package com.beside.groubing.groubingserver.domain.feed.dao

import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoCompleteMember.bingoCompleteMember
import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoItem.bingoItem
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FeedListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findRecentCompleterMemberIds(memberIds: List<Long>, isFriend: Boolean): List<Long> {
        val filter = if (isFriend) inFilter(memberIds) else notInFilter(memberIds)
        return queryFactory.selectDistinct(bingoCompleteMember.memberId)
            .from(bingoCompleteMember)
            .where(filter)
            .limit(MAX_COMPLETERS)
            .fetch()
    }

    fun findCompletedFeedItems(memberIds: List<Long>): List<FeedItemInfo> {
        if (memberIds.isEmpty()) return emptyList()
        return queryFactory.select(
            QFeedItemInfo(bingoCompleteMember.memberId, bingoItem.title)
        )
            .from(bingoItem)
            .innerJoin(bingoItem.completeMembers, bingoCompleteMember)
            .where(
                bingoCompleteMember.memberId.`in`(memberIds)
                    .and(bingoCompleteMember.active.isTrue)
                    .and(bingoItem.title.isNotNull)
                    .and(bingoItem.bingoBoard.active.isTrue)
                    .and(bingoItem.bingoBoard.period.isNotNull)
                    .and(bingoItem.bingoBoard.open.isTrue)
            )
            .fetch()
    }

    private fun inFilter(memberIds: List<Long>): BooleanExpression? =
        memberIds.takeIf { it.isNotEmpty() }
            ?.let { bingoCompleteMember.memberId.`in`(it).and(bingoCompleteMember.active.isTrue) }

    private fun notInFilter(memberIds: List<Long>): BooleanExpression? =
        memberIds.takeIf { it.isNotEmpty() }
            ?.let { bingoCompleteMember.memberId.notIn(it).and(bingoCompleteMember.active.isTrue) }

    companion object {
        private const val MAX_COMPLETERS = 20L
    }
}

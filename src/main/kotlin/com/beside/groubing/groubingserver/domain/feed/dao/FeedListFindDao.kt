package com.beside.groubing.groubingserver.domain.feed.dao

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem
import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoCompleteMember.bingoCompleteMember
import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoItem.bingoItem
import com.beside.groubing.groubingserver.domain.feed.payload.response.FeedResponse
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.QMember.member
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FeedListFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findFeeds(friendIds: List<Long> = emptyList(), isFriend: Boolean = false): List<FeedResponse> {
        val filter = if (isFriend) {
            inMemberIds(friendIds)
        } else {
            notInMemberIds(friendIds)
        }
        val completeMemberIds = extract20BingoItemCompleteMemberIds(filter)
        val bingoItems = getBingoItems(completeMemberIds)
        val members = get20Members(completeMemberIds)

        return buildFeedResponses(members, bingoItems)
    }

    private fun inMemberIds(friendIds: List<Long>): BooleanExpression? =
        friendIds.takeIf { it.isNotEmpty() }?.let { bingoCompleteMember.memberId.`in`(it) }

    private fun notInMemberIds(friendIds: List<Long>): BooleanExpression? =
        friendIds.takeIf { it.isNotEmpty() }?.let { bingoCompleteMember.memberId.notIn(it) }

    private fun extract20BingoItemCompleteMemberIds(filter: BooleanExpression?): List<Long> =
        queryFactory.selectDistinct(bingoCompleteMember.memberId)
            .from(bingoCompleteMember)
            .where(filter)
            .limit(20)
            .fetch()

    private fun getBingoItems(completeMemberIds: List<Long>): MutableList<BingoItem> =
        queryFactory.selectFrom(bingoItem)
            .where(
                bingoItem.completeMembers.any().memberId.`in`(completeMemberIds)
                    .and(bingoItem.bingoBoard.active.eq(true))
                    .and(bingoItem.bingoBoard.period.isNotNull)
            )
            .fetch()

    private fun get20Members(completeMemberIds: List<Long>): List<Member> =
        queryFactory.selectFrom(member)
            .where(member.id.`in`(completeMemberIds))
            .fetch()

    private fun buildFeedResponses(members: List<Member>, bingoItems: MutableList<BingoItem>): List<FeedResponse> =
        members.map { member ->
            val filteredBingoItems = bingoItems.filter {
                it.completeMembers.any { bingoCompleteMember -> bingoCompleteMember.memberId == member.id }
            }
            FeedResponse.create(member, filteredBingoItems.shuffled().take(5))
        }
}


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
    fun findFeeds(friendIds: List<Long> = emptyList()): List<FeedResponse> {
        val completeMemberIds = extract20BingoItemCompleteMemberIds(inMemberIds(friendIds))
        val bingoItems = getBingoItems(completeMemberIds)
        val members = get20Members(completeMemberIds)

        return members.map { member ->
            FeedResponse.create(member,
                bingoItems.filter {
                    it.completeMembers.any { bingoCompleteMember -> bingoCompleteMember.memberId == member.id }
                }.shuffled()
                    .take(5)
            )
        }
    }

    private fun getBingoItems(completeMemberIds: List<Long>): MutableList<BingoItem> {
        return queryFactory.selectFrom(bingoItem)
            .where(bingoItem.completeMembers.any().memberId.`in`(completeMemberIds))
            .fetch()
    }

    private fun extract20BingoItemCompleteMemberIds(inMemberIds: BooleanExpression?): List<Long> {
        return queryFactory.selectDistinct(bingoCompleteMember.memberId)
            .from(bingoCompleteMember)
            .where(inMemberIds)
            .limit(20)
            .fetch()
    }

    private fun get20Members(completeBingoMemberIds: List<Long>): List<Member> {
        return queryFactory.selectFrom(member)
            .where(member.id.`in`(completeBingoMemberIds))
            .fetch()
    }

    private fun inMemberIds(friendIds: List<Long>): BooleanExpression? {
        if (friendIds.isEmpty()) {
            return null
        }
        return bingoCompleteMember.memberId.`in`(friendIds)
    }
}

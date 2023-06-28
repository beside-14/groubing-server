package com.beside.groubing.groubingserver.domain.friendship.application

import com.beside.groubing.groubingserver.domain.friendship.domain.FriendshipStatus
import com.beside.groubing.groubingserver.domain.friendship.domain.QFriendship.friendship
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class FriendshipFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findFriendsById(id: Long, pageable: Pageable): Page<Member> {
        return find(id, pageable)
    }

    fun findFriendsByIdAndNickname(id: Long, pageable: Pageable, nickname: String): Page<Member> {
        return find(id, pageable) { builder ->
            builder.and(
                friendship.inviter.nickname.like("%$nickname%").or(friendship.invitee.nickname.like("%$nickname%"))
            )
        }
    }

    private fun find(
        id: Long,
        pageable: Pageable,
        predicateFunc: ((BooleanBuilder) -> BooleanBuilder)? = null
    ): Page<Member> {
        val query = queryFactory.from(friendship)
            .innerJoin(friendship.inviter).fetchJoin()
            .innerJoin(friendship.invitee).fetchJoin()
        var predicate = BooleanBuilder()
            .and(friendship.inviter.id.eq(id).or(friendship.invitee.id.eq(id)))
            .and(friendship.status.eq(FriendshipStatus.ACCEPT))

        takeIf { predicateFunc != null }?.apply { predicate = predicateFunc!!.invoke(predicate) }

        val friendships = query.select(friendship)
            .where(predicate)
            .orderBy(friendship.createdDate.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
        val inviters = friendships.map { friendship -> friendship.inviter }.filter { inviter -> inviter.id != id }
        val invitees = friendships.map { friendship -> friendship.invitee }.filter { invitee -> invitee.id != id }

        return PageImpl(inviters + invitees, pageable, countByTotal(predicate))
    }

    private fun countByTotal(predicate: Predicate?): Long {
        return queryFactory
            .select(friendship.count())
            .from(friendship)
            .innerJoin(friendship.inviter)
            .innerJoin(friendship.invitee)
            .where(predicate)
            .fetch()
            .first()
    }
}

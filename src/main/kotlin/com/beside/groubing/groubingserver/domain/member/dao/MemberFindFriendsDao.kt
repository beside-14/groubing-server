package com.beside.groubing.groubingserver.domain.member.dao

import com.beside.groubing.groubingserver.domain.friendship.domain.FriendshipStatus
import com.beside.groubing.groubingserver.domain.friendship.domain.QFriendship.friendship
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.QMember.member
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.core.types.Predicate
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class MemberFindFriendsDao(
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
        predicateFunc: ((BooleanBuilder) -> Predicate)? = null
    ): Page<Member> {
        val selectQuery = queryFactory.from(friendship)
            .innerJoin(friendship.inviter, member).fetchJoin()
            .innerJoin(friendship.invitee, member).fetchJoin()
            .orderBy(friendship.createdDate.desc())
        val pagingQuery = selectQuery.offset(pageable.offset).limit(pageable.pageSize.toLong())

        if (predicateFunc != null) {
            val builder = BooleanBuilder()
                .and(friendship.inviter.id.eq(id).or(friendship.invitee.id.eq(id)))
                .and(friendship.status.eq(FriendshipStatus.ACCEPT))
            val predicate = predicateFunc.invoke(builder)
            selectQuery.where(predicate)
            pagingQuery.where(predicate)
        }

        val inviters = pagingQuery.transform(GroupBy.groupBy(friendship.id).list(friendship.inviter))
        val invitees = pagingQuery.transform(GroupBy.groupBy(friendship.id).list(friendship.invitee))
        val friends = inviters.filter { inviter -> inviter.id != id } + invitees.filter { invitee -> invitee.id != id }
        return PageImpl(friends, pageable, selectQuery.fetch().count().toLong())
    }
}

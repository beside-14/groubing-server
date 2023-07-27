package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import com.beside.groubing.groubingserver.domain.friend.domain.QFriend.friend
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FriendFindDao(
    private val queryFactory: JPAQueryFactory,
    private val friendRepository: FriendRepository
) {
    fun findByFriendships(inviterId: Long, inviteeId: Long): List<Friend> {
        return queryFactory.selectFrom(friend)
            .where(
                friend.inviter.id.eq(inviterId).and(friend.invitee.id.eq(inviteeId))
                    .or(friend.inviter.id.eq(inviteeId).and(friend.invitee.id.eq(inviterId)))
            ).fetch()
    }

    fun findById(id: Long): Friend {
        val friend = friendRepository.findById(id).orElseThrow { FriendInputException("존재하지 않는 친구 요청입니다.") }
        if (friend.status.isAccept() || friend.status.isReject()) throw FriendInputException("이미 처리된 친구 요청입니다.")
        return friend
    }

    fun findAllById(
        id: Long,
        predicateFunc: ((BooleanBuilder) -> BooleanBuilder)? = null
    ): List<Member> {
        val query = queryFactory.from(friend)
            .innerJoin(friend.inviter).fetchJoin()
            .innerJoin(friend.invitee).fetchJoin()
        var predicate = BooleanBuilder()
            .and(friend.inviter.id.eq(id).or(friend.invitee.id.eq(id)))
            .and(friend.status.eq(FriendStatus.ACCEPT))

        takeIf { predicateFunc != null }?.apply { predicate = predicateFunc!!.invoke(predicate) }

        val friendships = query.select(friend)
            .where(predicate)
            .orderBy(friend.createdDate.desc())
            .fetch()
        val inviters = friendships.map { friendship -> friendship.inviter }.filter { inviter -> inviter.id != id }
        val invitees = friendships.map { friendship -> friendship.invitee }.filter { invitee -> invitee.id != id }

        return inviters + invitees
    }
}

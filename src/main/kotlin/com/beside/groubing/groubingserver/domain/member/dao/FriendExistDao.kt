package com.beside.groubing.groubingserver.domain.member.dao

import com.beside.groubing.groubingserver.domain.member.domain.QFriend.friend
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FriendExistDao(
    private val queryFactory: JPAQueryFactory
) {
    fun existByInviterOrInvitee(inviterId: Long, inviteeId: Long): Boolean {
        val result = queryFactory.select(friend.count())
            .from(friend)
            .where(
                friend.inviter.id.eq(inviterId).and(friend.invitee.id.eq(inviteeId))
                    .or(friend.inviter.id.eq(inviteeId).and(friend.invitee.id.eq(inviterId)))
            )
            .fetchOne()

        return result != null && result > 0
    }
}

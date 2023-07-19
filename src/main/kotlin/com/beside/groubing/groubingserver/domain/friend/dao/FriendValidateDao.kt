package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.domain.blocked.dao.BlockedMemberExistDao
import com.beside.groubing.groubingserver.domain.friend.domain.QFriend.friend
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FriendValidateDao(
    private val blockedMemberExistDao: BlockedMemberExistDao,
    private val queryFactory: JPAQueryFactory
) {
    private val message: String = "친구 신청이 불가능한 유저입니다."

    fun validate(inviterId: Long, inviteeId: Long) {
        val isBlocked = blockedMemberExistDao.existByRequesterOrTargetMember(inviterId, inviteeId)
        if (isBlocked) throw FriendInputException(message)

        val isFriend = queryFactory.select(friend.count())
            .from(friend)
            .where(
                friend.inviter.id.eq(inviterId).and(friend.invitee.id.eq(inviteeId))
                    .or(friend.inviter.id.eq(inviteeId).and(friend.invitee.id.eq(inviterId)))
            )
            .fetchOne()

        if (isFriend != null && isFriend > 0) throw FriendInputException(message)
    }
}

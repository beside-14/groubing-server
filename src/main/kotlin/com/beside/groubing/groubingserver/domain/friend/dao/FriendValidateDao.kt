package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.domain.blocked.dao.BlockedMemberExistDao
import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.QFriend.friend
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FriendValidateDao(
    private val blockedMemberExistDao: BlockedMemberExistDao,
    private val queryFactory: JPAQueryFactory
) {

    fun validate(inviterId: Long, inviteeId: Long): Friend? {
        blockedMemberExistDao.existByRequesterOrTargetMember(inviterId, inviteeId)

        val friendships = queryFactory
            .selectFrom(friend)
            .where(
                friend.inviter.id.eq(inviterId).and(friend.invitee.id.eq(inviteeId))
                    .or(friend.inviter.id.eq(inviteeId).and(friend.invitee.id.eq(inviterId)))
            )
            .fetch()

        return when (friendships.isEmpty()) {
            true -> null
            false -> {
                val isFriend = friendships.any { friendship ->
                    friendship.status.isAccept() || friendship.status.isPending()
                }
                if (isFriend) throw FriendInputException("이미 친구이거나 친구 수락 대기 상태입니다.")
                friendships.find { friendship -> friendship.inviter.id == inviterId && friendship.invitee.id == inviteeId && friendship.status.isReject() }
            }
        }
    }
}

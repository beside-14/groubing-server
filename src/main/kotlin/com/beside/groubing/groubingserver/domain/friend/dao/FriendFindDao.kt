package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import com.beside.groubing.groubingserver.domain.friend.domain.QFriend.friend
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FriendFindDao(
    private val queryFactory: JPAQueryFactory,
    private val friendRepository: FriendRepository
) {
    fun findByFriends(inviterId: Long, inviteeId: Long): List<Friend> {
        val isActive = friend.invitee.active.isTrue.and(friend.inviter.active.isTrue)
        val isSent = friend.inviter.id.eq(inviterId).and(friend.invitee.id.eq(inviteeId))
        val isReceived = friend.inviter.id.eq(inviteeId).and(friend.invitee.id.eq(inviterId))
        return queryFactory.selectFrom(friend)
            .where(isActive.and(isSent.or(isReceived)))
            .fetch()
    }

    fun findById(id: Long): Friend {
        return friendRepository.findById(id).orElseThrow { FriendInputException("존재하지 않는 친구 요청입니다.") }
    }

    fun findAllByInviterIdOrInviteeId(memberId: Long): Map<Long, Member> {
        val isActive = friend.inviter.active.isTrue.and(friend.invitee.active.isTrue)
        val isAccept = friend.status.eq(FriendStatus.ACCEPT)
        val isInviter = friend.inviter.id.eq(memberId)
        val isInvitee = friend.invitee.id.eq(memberId)
        val friends = queryFactory.selectFrom(friend)
            .innerJoin(friend.inviter).fetchJoin()
            .innerJoin(friend.invitee).fetchJoin()
            .where(isActive.and(isAccept).and(isInviter.or(isInvitee)))
            .orderBy(friend.createdDate.desc())
            .fetch()

        val inviters =
            friends.filter { friend -> friend.inviter.id != memberId && friend.inviter.active }
                .associate { friend -> friend.id to friend.inviter }

        val invitees =
            friends.filter { friend -> friend.invitee.id != memberId && friend.inviter.active }
                .associate { friend -> friend.id to friend.invitee }

        return inviters + invitees
    }

    fun findAllByInviteeId(inviteeId: Long): List<Friend> {
        return queryFactory.selectFrom(friend)
            .innerJoin(friend.invitee)
            .where(friend.invitee.id.eq(inviteeId).and(friend.invitee.active.isTrue))
            .orderBy(friend.createdDate.desc())
            .fetch()
    }

    fun findAllByInviterId(inviterId: Long): List<Friend> {
        return queryFactory.selectFrom(friend)
            .innerJoin(friend.inviter)
            .where(friend.inviter.id.eq(inviterId).and(friend.inviter.active.isTrue))
            .orderBy(friend.createdDate.desc())
            .fetch()
    }
}

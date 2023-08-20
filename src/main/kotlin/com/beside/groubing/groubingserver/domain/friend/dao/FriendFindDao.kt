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
        return queryFactory.selectFrom(friend)
            .where(
                friend.inviter.id.eq(inviterId).and(friend.invitee.id.eq(inviteeId))
                    .or(friend.inviter.id.eq(inviteeId).and(friend.invitee.id.eq(inviterId)))
            ).fetch()
    }

    fun findById(id: Long): Friend {
        return friendRepository.findById(id).orElseThrow { FriendInputException("존재하지 않는 친구 요청입니다.") }
    }

    fun findAllById(id: Long): Map<Long, Member> {
        val friends = queryFactory
            .selectFrom(friend)
            .innerJoin(friend.inviter).fetchJoin()
            .innerJoin(friend.invitee).fetchJoin()
            .where(friend.inviter.id.eq(id).or(friend.invitee.id.eq(id)).and(friend.status.eq(FriendStatus.ACCEPT)))
            .orderBy(friend.createdDate.desc())
            .fetch()

        val inviters =
            friends.filter { friend -> friend.inviter.id != id }.associate { friend -> friend.id to friend.inviter }

        val invitees =
            friends.filter { friend -> friend.invitee.id != id }.associate { friend -> friend.id to friend.invitee }

        return inviters + invitees
    }
}

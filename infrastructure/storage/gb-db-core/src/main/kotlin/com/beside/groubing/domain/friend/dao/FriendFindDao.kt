package com.beside.groubing.domain.friend.dao

import com.beside.groubing.domain.friend.domain.FriendStatus
import com.beside.groubing.domain.friend.entity.QFriendEntity.friendEntity
import com.beside.groubing.domain.member.entity.QMemberEntity.memberEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FriendFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findAllAcceptedOf(memberId: Long): List<FriendMemberInfo> {
        return queryFactory.select(
            QFriendMemberInfo(
                friendEntity.id,
                memberEntity.id,
                memberEntity.nickname,
                memberEntity.profile.fileName,
                friendEntity.status
            )
        )
            .from(friendEntity, memberEntity)
            .leftJoin(memberEntity.profile)
            .where(
                friendEntity.status.eq(FriendStatus.ACCEPT)
                    .and(
                        friendEntity.inviterId.eq(memberId).and(memberEntity.id.eq(friendEntity.inviteeId))
                            .or(friendEntity.inviteeId.eq(memberId).and(memberEntity.id.eq(friendEntity.inviterId)))
                    )
                    .and(memberEntity.active.isTrue)
            )
            .orderBy(friendEntity.createdDate.desc())
            .fetch()
    }

    fun findAllReceivedBy(inviteeId: Long, statuses: Set<FriendStatus>): List<FriendMemberInfo> {
        return queryFactory.select(
            QFriendMemberInfo(
                friendEntity.id,
                memberEntity.id,
                memberEntity.nickname,
                memberEntity.profile.fileName,
                friendEntity.status
            )
        )
            .from(friendEntity)
            .innerJoin(memberEntity).on(memberEntity.id.eq(friendEntity.inviterId))
            .leftJoin(memberEntity.profile)
            .where(
                friendEntity.inviteeId.eq(inviteeId)
                    .and(friendEntity.status.`in`(statuses))
                    .and(memberEntity.active.isTrue)
            )
            .orderBy(friendEntity.createdDate.desc())
            .fetch()
    }

    fun findAllSentBy(inviterId: Long, statuses: Set<FriendStatus>): List<FriendMemberInfo> {
        return queryFactory.select(
            QFriendMemberInfo(
                friendEntity.id,
                memberEntity.id,
                memberEntity.nickname,
                memberEntity.profile.fileName,
                friendEntity.status
            )
        )
            .from(friendEntity)
            .innerJoin(memberEntity).on(memberEntity.id.eq(friendEntity.inviteeId))
            .leftJoin(memberEntity.profile)
            .where(
                friendEntity.inviterId.eq(inviterId)
                    .and(friendEntity.status.`in`(statuses))
                    .and(memberEntity.active.isTrue)
            )
            .orderBy(friendEntity.createdDate.desc())
            .fetch()
    }
}

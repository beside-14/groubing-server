package com.beside.groubing.groubingserver.domain.friend.dao

import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import com.beside.groubing.groubingserver.domain.friend.entity.QFriendEntity.friendEntity
import com.beside.groubing.groubingserver.domain.member.entity.QMemberEntity.memberEntity
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
                memberEntity.email,
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
            )
            .orderBy(friendEntity.createdDate.desc())
            .fetch()
    }

    fun findAllReceivedBy(inviteeId: Long): List<FriendMemberInfo> {
        return queryFactory.select(
            QFriendMemberInfo(
                friendEntity.id,
                memberEntity.id,
                memberEntity.email,
                memberEntity.nickname,
                memberEntity.profile.fileName,
                friendEntity.status
            )
        )
            .from(friendEntity)
            .innerJoin(memberEntity).on(memberEntity.id.eq(friendEntity.inviterId))
            .leftJoin(memberEntity.profile)
            .where(friendEntity.inviteeId.eq(inviteeId))
            .orderBy(friendEntity.createdDate.desc())
            .fetch()
    }

    fun findAllSentBy(inviterId: Long): List<FriendMemberInfo> {
        return queryFactory.select(
            QFriendMemberInfo(
                friendEntity.id,
                memberEntity.id,
                memberEntity.email,
                memberEntity.nickname,
                memberEntity.profile.fileName,
                friendEntity.status
            )
        )
            .from(friendEntity)
            .innerJoin(memberEntity).on(memberEntity.id.eq(friendEntity.inviteeId))
            .leftJoin(memberEntity.profile)
            .where(friendEntity.inviterId.eq(inviterId))
            .orderBy(friendEntity.createdDate.desc())
            .fetch()
    }
}

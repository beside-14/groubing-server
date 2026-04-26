package com.beside.groubing.domain.notification.dao

import com.beside.groubing.domain.member.entity.QMemberEntity.memberEntity as member
import com.beside.groubing.domain.notification.entity.QNotificationEntity.notificationEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class NotificationFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findNotifications(bingoBoardIds: List<Long>, myMemberId: Long): List<NotificationWithMemberProfile> {
        return queryFactory.select(
            QNotificationWithMemberProfile(
                notificationEntity.bingoBoardId,
                notificationEntity.memberId,
                notificationEntity.message,
                member.profile.fileName
            )
        )
            .from(notificationEntity)
            .join(member).on(notificationEntity.memberId.eq(member.id))
            .leftJoin(member.profile)
            .where(
                notificationEntity.bingoBoardId.`in`(bingoBoardIds)
                    .and(notificationEntity.memberId.ne(myMemberId))
                    .and(member.active.isTrue)
            )
            .orderBy(notificationEntity.createdDate.desc())
            .limit(MAX_NOTIFICATION_COUNT)
            .fetch()
    }

    companion object {
        private const val MAX_NOTIFICATION_COUNT = 30L
    }
}

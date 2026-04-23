package com.beside.groubing.groubingserver.domain.notification.dao

import com.beside.groubing.groubingserver.domain.member.entity.QMemberEntity.memberEntity as member
import com.beside.groubing.groubingserver.domain.notification.entity.QNotificationEntity.notificationEntity
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
            .where(notificationEntity.bingoBoardId.`in`(bingoBoardIds).and(member.active.isTrue))
            .orderBy(notificationEntity.createdDate.desc())
            .fetch()
            .filter { it.memberId != myMemberId }
            .take(30)
    }
}

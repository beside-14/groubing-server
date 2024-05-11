package com.beside.groubing.groubingserver.domain.notification.dao

import com.beside.groubing.groubingserver.domain.member.domain.QMember.member
import com.beside.groubing.groubingserver.domain.notification.domain.NotificationWithMemberProfile
import com.beside.groubing.groubingserver.domain.notification.domain.QNotification.notification
import com.beside.groubing.groubingserver.domain.notification.domain.QNotificationWithMemberProfile
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class NotificationFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findNotifications(bingoBoardIds: List<Long>, myMemberId: Long): List<NotificationWithMemberProfile> {
        return queryFactory.select(
            QNotificationWithMemberProfile(
                notification.bingoBoardId,
                notification.memberId,
                notification.message,
                member.profile.fileName
            )
        )
            .from(notification)
            .join(member).on(notification.memberId.eq(member.id))
            .leftJoin(member.profile)
            .where(notification.bingoBoardId.`in`(bingoBoardIds).and(member.active.isTrue))
            .orderBy(notification.createdDate.desc())
            .fetch()
            .filter { it.memberId != myMemberId }
            .take(30)
    }
}

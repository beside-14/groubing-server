package com.beside.groubing.groubingserver.domain.notification.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoCompleteEvent
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItemCancelEvent
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItemCompleteEvent
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.infra.fcm.application.FcmNotiService
import com.beside.groubing.groubingserver.infra.fcm.payload.FcmNotiRequestDto
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class BingoItemCompleteEventHandler(
    private val notificationRepository: NotificationRepository,

    private val memberFindDao: MemberFindDao,
    
    private val fcmNotiService: FcmNotiService
) {
    @Async
    @EventListener
    fun handle(event: BingoItemCompleteEvent) {
        val member = memberFindDao.findExistingMemberById(event.memberId)
        val message = "${member.nickname}님이 ${event.bingoBoardTitle} 빙고를 ${event.totalBingoCount} 빙고 달성했어요!"
        notificationRepository.save(Notification(bingoBoardId = event.bingoBoardId, memberId = event.memberId,
            message = message)
        )
        val members = memberFindDao.findPushNotificationMembers(event.otherMemberIds)
        sendPushNotification(members = members, message = message, bingoBoardId = event.bingoBoardId)
    }

    @Async
    @EventListener
    fun handle(event: BingoCompleteEvent) {
        val member = memberFindDao.findExistingMemberById(event.memberId)
        val message = "${member.nickname}님이 ${event.bingoBoardTitle} 빙고의 목표 빙고 수를 달성했어요!"
        notificationRepository.save(Notification(bingoBoardId = event.bingoBoardId, memberId = event.memberId,
            message = message)
        )
        val members = memberFindDao.findPushNotificationMembers(event.otherMemberIds)
        sendPushNotification(members = members, message = message, bingoBoardId = event.bingoBoardId)
    }

    @Async
    @EventListener
    fun handle(event: BingoItemCancelEvent) {
        val member = memberFindDao.findExistingMemberById(event.memberId)
        val message = "${member.nickname}님이 ${event.bingoBoardTitle} 빙고에서 달성한 빙고 중 ${event.bingoItemTitle} 빙고 아이템을 취소했어요."
        notificationRepository.save(Notification(bingoBoardId = event.bingoBoardId, memberId = event.memberId,
            message = message)
        )
        val members = memberFindDao.findPushNotificationMembers(event.otherMemberIds)
        sendPushNotification(members = members, message = message, bingoBoardId = event.bingoBoardId)
    }

    private fun sendPushNotification(members: List<Member>, message: String, bingoBoardId: Long) {
        fcmNotiService.sendNotificationMessage(
            members.map { 
                FcmNotiRequestDto(
                    fcmToken = it.fcmToken!!,
                    title = "GROUBING 알림",
                    message = message,
                    screenType = ScreenType.BINGO_BOARD,
                    dataId = bingoBoardId
                )
            }
        )
    }
}

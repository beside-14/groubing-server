package com.beside.groubing.groubingserver.domain.notification.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoCompleteEvent
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItemCancelEvent
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItemCompleteEvent
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class BingoItemCompleteEventHandler(
    private val notificationRepository: NotificationRepository,

    private val memberFindDao: MemberFindDao
) {
    @Async
    @EventListener
    fun handle(event: BingoItemCompleteEvent) {
        val member = memberFindDao.findExistingMemberById(event.memberId)
        notificationRepository.save(Notification(bingoBoardId = event.bingoBoardId, memberId = event.memberId,
            message = "${member.nickname}님이 ${event.bingoBoardTitle} 빙고를 ${event.totalBingoCount} 빙고 달성했어요!")
        )
    }

    @Async
    @EventListener
    fun handle(event: BingoCompleteEvent) {
        val member = memberFindDao.findExistingMemberById(event.memberId)
        notificationRepository.save(Notification(bingoBoardId = event.bingoBoardId, memberId = event.memberId,
            message = "${member.nickname}님이 ${event.bingoBoardTitle} 빙고의 목표 빙고 수를 달성했어요!")
        )
    }

    @Async
    @EventListener
    fun handle(event: BingoItemCancelEvent) {
        val member = memberFindDao.findExistingMemberById(event.memberId)
        notificationRepository.save(Notification(bingoBoardId = event.bingoBoardId, memberId = event.memberId,
            message = "${member.nickname}님이 ${event.bingoBoardTitle} 빙고에서 달성한 빙고 중 ${event.bingoItemTitle} 빙고 아이템을 취소했어요.")
        )
    }
}

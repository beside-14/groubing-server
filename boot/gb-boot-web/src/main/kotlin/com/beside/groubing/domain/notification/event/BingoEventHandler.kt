package com.beside.groubing.domain.notification.event

import com.beside.groubing.domain.bingo.event.BingoCompleteEvent
import com.beside.groubing.domain.bingo.event.BingoLineCancelEvent
import com.beside.groubing.domain.bingo.event.BingoLineCompleteEvent
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.notification.domain.Notification
import com.beside.groubing.domain.notification.domain.port.NotificationRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class BingoEventHandler(
    private val notificationRepository: NotificationRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    @Async
    @EventListener
    fun handle(event: BingoLineCompleteEvent) {
        val member = memberQueryRepository.findById(event.memberId)
        val message = "${member.nickname}님이 ${event.bingoBoardTitle} 빙고를 ${event.totalBingoCount} 빙고 달성했어요!"
        notificationRepository.save(
            Notification.create(bingoBoardId = event.bingoBoardId, memberId = event.memberId, message = message)
        )
    }

    @Async
    @EventListener
    fun handle(event: BingoLineCancelEvent) {
        val member = memberQueryRepository.findById(event.memberId)
        val message = "${member.nickname}님이 ${event.bingoBoardTitle} 빙고에서 달성한 빙고 중 ${event.bingoItemTitle} 빙고 아이템을 취소했어요."
        notificationRepository.save(
            Notification.create(bingoBoardId = event.bingoBoardId, memberId = event.memberId, message = message)
        )
    }

    @Async
    @EventListener
    fun handle(event: BingoCompleteEvent) {
        val member = memberQueryRepository.findById(event.memberId)
        val message = "${member.nickname}님이 ${event.bingoBoardTitle} 빙고의 목표 빙고 수를 달성했어요!"
        notificationRepository.save(
            Notification.create(bingoBoardId = event.bingoBoardId, memberId = event.memberId, message = message)
        )
    }
}

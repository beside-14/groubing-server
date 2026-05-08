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
        save(event.bingoBoardId, event.memberId, event.toMessage(nicknameOf(event.memberId)))
    }

    @Async
    @EventListener
    fun handle(event: BingoLineCancelEvent) {
        save(event.bingoBoardId, event.memberId, event.toMessage(nicknameOf(event.memberId)))
    }

    @Async
    @EventListener
    fun handle(event: BingoCompleteEvent) {
        save(event.bingoBoardId, event.memberId, event.toMessage(nicknameOf(event.memberId)))
    }

    private fun nicknameOf(memberId: Long): String = memberQueryRepository.findById(memberId).nickname

    private fun save(bingoBoardId: Long, memberId: Long, message: String) {
        notificationRepository.save(
            Notification.create(bingoBoardId = bingoBoardId, memberId = memberId, message = message)
        )
    }
}

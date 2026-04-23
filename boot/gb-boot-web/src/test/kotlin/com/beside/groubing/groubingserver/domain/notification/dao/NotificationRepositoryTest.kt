package com.beside.groubing.groubingserver.domain.notification.dao

import com.beside.groubing.groubingserver.domain.notification.domain.Notification
import com.beside.groubing.groubingserver.domain.notification.domain.NotificationRepository
import com.beside.groubing.groubingserver.persistence.LocalPersistenceTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.PageRequest

@LocalPersistenceTest
class NotificationRepositoryTest(
    private val notificationRepository: NotificationRepository
) : FunSpec({
    beforeEach {
        notificationRepository.saveAll(
            listOf(
                Notification(bingoBoardId = 1L, memberId = 1L, message = "alarm1-1"),
                Notification(bingoBoardId = 1L, memberId = 2L, message = "alarm1-2"),
                Notification(bingoBoardId = 1L, memberId = 3L, message = "alarm1-3"),
                Notification(bingoBoardId = 1L, memberId = 4L, message = "alarm1-4"),
                Notification(bingoBoardId = 2L, memberId = 1L, message = "alarm2-1"),
                Notification(bingoBoardId = 2L, memberId = 4L, message = "alarm2-2"),
                Notification(bingoBoardId = 2L, memberId = 5L, message = "alarm2-3"),
                Notification(bingoBoardId = 3L, memberId = 2L, message = "alarm3-1"),
                Notification(bingoBoardId = 3L, memberId = 6L, message = "alarm3-2"),
                Notification(bingoBoardId = 4L, memberId = 7L, message = "alarm4"),
                Notification(bingoBoardId = 5L, memberId = 8L, message = "alarm5"),
                Notification(bingoBoardId = 6L, memberId = 9L, message = "alarm6"),
                Notification(bingoBoardId = 7L, memberId = 10L, message = "alarm7-1"),
                Notification(bingoBoardId = 7L, memberId = 11L, message = "alarm7-2")
            )
        )
    }

    test("Notification 정상 조회 테스트") {
        val notificationsPage = notificationRepository.findByBingoBoardIdInAndMemberIdNotOrderByCreatedDateDesc(bingoBoardIds = listOf(1L, 2L, 4L, 5L, 6L),
            memberId = 1L, pageable = PageRequest.of(1, 3))
        notificationsPage.totalPages shouldBe 3
        notificationsPage.content.last().message shouldBe "alarm1-4"
    }
})

package com.beside.groubing.groubingserver.domain.notification.dao

import com.beside.groubing.groubingserver.aMember
import com.beside.groubing.groubingserver.config.QuerydslConfig
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.notification.domain.Notification
import com.beside.groubing.groubingserver.domain.notification.domain.NotificationRepository
import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo
import com.beside.groubing.groubingserver.persistence.LocalPersistenceTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@LocalPersistenceTest
@Import(QuerydslConfig::class, NotificationFindDao::class)
class NotificationFindDaoTest(
    private val memberRepository: MemberRepository,

    private val notificationRepository: NotificationRepository,

    private val notificationFindDao: NotificationFindDao
) : FunSpec({
    beforeEach {
        memberRepository.saveAll(
            (1L..50L).map { aMember(it) }
        )

        val member1 = memberRepository.findById(1L).get()
        member1.profile = FileInfo.create(
            directory = "file/profile",
            fileName = "profile1.jpg",
            originalName = "profile1.jpg",
        )

        val member2 = memberRepository.findById(2L).get()
        member2.profile = FileInfo.create(
            directory = "file/profile",
            fileName = "profile2.jpg",
            originalName = "profile2.jpg",
        )

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
        val notifications = notificationFindDao.findNotifications(
            bingoBoardIds = listOf(1L, 2L, 4L, 5L, 6L),
            myMemberId = 1L
        )

        notifications.size shouldBe 8
        notifications.first { it.memberId == 2L }.profileUrl shouldBe "profile2.jpg"
    }
})

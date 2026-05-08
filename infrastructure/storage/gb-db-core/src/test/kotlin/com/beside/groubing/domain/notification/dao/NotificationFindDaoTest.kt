package com.beside.groubing.domain.notification.dao

import com.beside.groubing.aMember
import com.beside.groubing.global.config.QuerydslConfig
import com.beside.groubing.domain.member.repository.MemberJpaRepository
import com.beside.groubing.domain.notification.entity.NotificationEntity
import com.beside.groubing.domain.notification.repository.NotificationJpaRepository
import com.beside.groubing.global.domain.file.domain.FileInfo
import com.beside.groubing.global.domain.file.entity.FileInfoEntity
import com.beside.groubing.persistence.PersistenceTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@PersistenceTest
@Import(QuerydslConfig::class, NotificationFindDao::class)
class NotificationFindDaoTest(
    private val memberRepository: MemberJpaRepository,

    private val notificationJpaRepository: NotificationJpaRepository,

    private val notificationFindDao: NotificationFindDao
) : FunSpec({
    beforeEach {
        memberRepository.saveAll(
            (1L..50L).map { aMember(it) }
        )

        val member1 = memberRepository.findById(1L).get()
        member1.profile = FileInfoEntity.from(
            FileInfo.create(
                directory = "file/profile",
                fileName = "profile1.jpg",
                originalName = "profile1.jpg",
            )
        )

        val member2 = memberRepository.findById(2L).get()
        member2.profile = FileInfoEntity.from(
            FileInfo.create(
                directory = "file/profile",
                fileName = "profile2.jpg",
                originalName = "profile2.jpg",
            )
        )

        notificationJpaRepository.saveAll(
            listOf(
                NotificationEntity(bingoBoardId = 1L, memberId = 1L, message = "alarm1-1"),
                NotificationEntity(bingoBoardId = 1L, memberId = 2L, message = "alarm1-2"),
                NotificationEntity(bingoBoardId = 1L, memberId = 3L, message = "alarm1-3"),
                NotificationEntity(bingoBoardId = 1L, memberId = 4L, message = "alarm1-4"),
                NotificationEntity(bingoBoardId = 2L, memberId = 1L, message = "alarm2-1"),
                NotificationEntity(bingoBoardId = 2L, memberId = 4L, message = "alarm2-2"),
                NotificationEntity(bingoBoardId = 2L, memberId = 5L, message = "alarm2-3"),
                NotificationEntity(bingoBoardId = 3L, memberId = 2L, message = "alarm3-1"),
                NotificationEntity(bingoBoardId = 3L, memberId = 6L, message = "alarm3-2"),
                NotificationEntity(bingoBoardId = 4L, memberId = 7L, message = "alarm4"),
                NotificationEntity(bingoBoardId = 5L, memberId = 8L, message = "alarm5"),
                NotificationEntity(bingoBoardId = 6L, memberId = 9L, message = "alarm6"),
                NotificationEntity(bingoBoardId = 7L, memberId = 10L, message = "alarm7-1"),
                NotificationEntity(bingoBoardId = 7L, memberId = 11L, message = "alarm7-2")
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

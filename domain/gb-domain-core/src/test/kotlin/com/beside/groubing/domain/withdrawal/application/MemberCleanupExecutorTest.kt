package com.beside.groubing.domain.withdrawal.application

import com.beside.groubing.domain.auth.domain.port.SocialInfoRepository
import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.domain.common.file.application.FileStorage
import com.beside.groubing.domain.common.file.domain.FileInfo
import com.beside.groubing.domain.friend.domain.port.FriendCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.notification.domain.port.NotificationRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify

class MemberCleanupExecutorTest : BehaviorSpec({
    val notificationRepository = mockk<NotificationRepository>(relaxed = true)
    val friendCommandRepository = mockk<FriendCommandRepository>(relaxed = true)
    val blockedMemberRepository = mockk<BlockedMemberRepository>(relaxed = true)
    val socialInfoRepository = mockk<SocialInfoRepository>(relaxed = true)
    val memberCommandRepository = mockk<MemberCommandRepository>()
    val executor = MemberCleanupExecutor(
        notificationRepository,
        friendCommandRepository,
        blockedMemberRepository,
        socialInfoRepository,
        memberCommandRepository
    )

    beforeSpec { mockkObject(FileStorage.Companion) }
    afterSpec { unmockkObject(FileStorage.Companion) }

    Given("프로필이 있는 만료 회원이 주어졌을 때") {
        val memberId = 7L
        val previousProfile = mockk<FileInfo>()
        every { memberCommandRepository.tombstone(memberId, any()) } returns previousProfile
        every { FileStorage.delete(previousProfile) } just Runs

        When("cleanup 을 호출하면") {
            executor.cleanup(memberId)

            Then("연관 데이터 삭제 + tombstone + 프로필 물리파일 삭제가 수행된다") {
                verify(exactly = 1) { notificationRepository.deleteAllByMemberId(memberId) }
                verify(exactly = 1) { friendCommandRepository.deleteAllOf(memberId) }
                verify(exactly = 1) { blockedMemberRepository.deleteAllOf(memberId) }
                verify(exactly = 1) { socialInfoRepository.deleteAllByMemberId(memberId) }
                verify(exactly = 1) { memberCommandRepository.tombstone(memberId, any()) }
                verify(exactly = 1) { FileStorage.delete(previousProfile) }
            }
        }
    }

    Given("프로필이 없는 만료 회원이 주어졌을 때") {
        val memberId = 8L
        every { memberCommandRepository.tombstone(memberId, any()) } returns null

        When("cleanup 을 호출하면") {
            executor.cleanup(memberId)

            Then("물리파일 삭제는 호출되지 않는다") {
                verify(exactly = 1) { memberCommandRepository.tombstone(memberId, any()) }
                verify(exactly = 0) { FileStorage.delete(any()) }
            }
        }
    }
})

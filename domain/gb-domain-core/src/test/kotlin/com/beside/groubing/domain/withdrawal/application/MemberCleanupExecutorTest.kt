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
import io.mockk.verifyOrder

class MemberCleanupExecutorTest : BehaviorSpec({
    val notificationRepository = mockk<NotificationRepository>(relaxed = true)
    val friendCommandRepository = mockk<FriendCommandRepository>(relaxed = true)
    val blockedMemberRepository = mockk<BlockedMemberRepository>(relaxed = true)
    val socialInfoRepository = mockk<SocialInfoRepository>(relaxed = true)
    val withdrawnMemberBingoCleaner = mockk<WithdrawnMemberBingoCleaner>()
    val memberCommandRepository = mockk<MemberCommandRepository>()
    val executor = MemberCleanupExecutor(
        notificationRepository,
        friendCommandRepository,
        blockedMemberRepository,
        socialInfoRepository,
        withdrawnMemberBingoCleaner,
        memberCommandRepository
    )

    beforeSpec { mockkObject(FileStorage.Companion) }
    afterSpec { unmockkObject(FileStorage.Companion) }

    Given("그룹 빙고에 잔존하고 프로필이 있는 만료 회원이 주어졌을 때") {
        val memberId = 7L
        val previousProfile = mockk<FileInfo>()
        every { withdrawnMemberBingoCleaner.cleanUpBoardsOf(memberId) } returns true
        every { memberCommandRepository.tombstone(memberId, any()) } returns previousProfile
        every { FileStorage.delete(previousProfile) } just Runs

        When("cleanup 을 호출하면") {
            executor.cleanup(memberId)

            Then("연관 데이터 삭제 → 빙고 정리 → tombstone → 프로필 물리파일 삭제 순으로 수행된다") {
                verifyOrder {
                    socialInfoRepository.deleteAllByMemberId(memberId)
                    withdrawnMemberBingoCleaner.cleanUpBoardsOf(memberId)
                    memberCommandRepository.tombstone(memberId, any())
                    FileStorage.delete(previousProfile)
                }
                verify(exactly = 1) { notificationRepository.deleteAllByMemberId(memberId) }
                verify(exactly = 1) { friendCommandRepository.deleteAllOf(memberId) }
                verify(exactly = 1) { blockedMemberRepository.deleteAllOf(memberId) }
                verify(exactly = 0) { memberCommandRepository.hardDelete(any()) }
            }
        }
    }

    Given("그룹 빙고에 잔존하지 않는 만료 회원이 주어졌을 때") {
        val memberId = 8L
        every { withdrawnMemberBingoCleaner.cleanUpBoardsOf(memberId) } returns false
        every { memberCommandRepository.hardDelete(memberId) } returns null

        When("cleanup 을 호출하면") {
            executor.cleanup(memberId)

            Then("tombstone 대신 회원을 완전 삭제하고 프로필 물리파일 삭제는 호출되지 않는다") {
                verify(exactly = 1) { memberCommandRepository.hardDelete(memberId) }
                verify(exactly = 0) { memberCommandRepository.tombstone(any(), any()) }
                verify(exactly = 0) { FileStorage.delete(any()) }
            }
        }
    }

    Given("그룹 빙고에 잔존하지 않고 프로필이 있는 만료 회원이 주어졌을 때") {
        val memberId = 9L
        val previousProfile = mockk<FileInfo>()
        every { withdrawnMemberBingoCleaner.cleanUpBoardsOf(memberId) } returns false
        every { memberCommandRepository.hardDelete(memberId) } returns previousProfile
        every { FileStorage.delete(previousProfile) } just Runs

        When("cleanup 을 호출하면") {
            executor.cleanup(memberId)

            Then("완전 삭제 후 이전 프로필 물리파일을 삭제한다") {
                verify(exactly = 1) { memberCommandRepository.hardDelete(memberId) }
                verify(exactly = 1) { FileStorage.delete(previousProfile) }
            }
        }
    }
})

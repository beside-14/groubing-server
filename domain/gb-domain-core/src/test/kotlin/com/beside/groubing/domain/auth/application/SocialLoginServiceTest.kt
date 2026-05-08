package com.beside.groubing.domain.auth.application

import com.beside.groubing.domain.auth.application.command.SocialLoginCommand
import com.beside.groubing.domain.auth.domain.SocialInfo
import com.beside.groubing.domain.auth.domain.SocialType
import com.beside.groubing.domain.auth.domain.port.SocialInfoRepository
import com.beside.groubing.domain.auth.domain.port.TokenManager
import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class SocialLoginServiceTest : BehaviorSpec({
    val mockSocialInfoRepository = mockk<SocialInfoRepository>()
    val mockMemberCommandRepository = mockk<MemberCommandRepository>()
    val mockMemberQueryRepository = mockk<MemberQueryRepository>()
    val mockTokenManager = mockk<TokenManager>()
    val socialLoginService = SocialLoginService(
        mockSocialInfoRepository,
        mockMemberCommandRepository,
        mockMemberQueryRepository,
        mockTokenManager
    )

    Given("SocialLoginService가 주어졌을 때") {
        fun prepareMock(existingMember: Member, existingSocialInfo: SocialInfo? = null) {
            every { mockMemberQueryRepository.findById(any()) } returns existingMember
            every { mockMemberCommandRepository.update(any()) } returns existingMember
            every { mockSocialInfoRepository.findBySocialIdAndSocialTypeOrNull(any(), any()) } returns existingSocialInfo
            every { mockTokenManager.generateAccessToken(any(), any()) } returns "token"
        }

        val fcmToken = "cFypG01m0s:APA91bEETmrwFTfkpscX3_qpYx03NE"
        fun createSocialLoginCommand(id: String, email: String, socialType: SocialType) =
            SocialLoginCommand(id, email, socialType, fcmToken)
        fun createMember(id: Long, email: String) = Member(
            id = id,
            email = email,
            password = "",
            nickname = "nickname",
            role = MemberRole.MEMBER,
            memberType = MemberType.SOCIAL,
            fcmToken = null,
            notificationReceive = true,
            active = true,
            profileUrl = null
        )

        When("이미 존재하는 사용자로 로그인하는 경우") {
            val memberId = 1L
            val socialId = "153262439"
            val email = "email@example.com"
            val existingMember = createMember(memberId, email)
            val existingSocialInfo = SocialInfo.of(0L, socialId, email, SocialType.KAKAO, memberId)

            prepareMock(existingMember, existingSocialInfo)
            val result = socialLoginService.login(createSocialLoginCommand(socialId, email, SocialType.KAKAO))

            Then("이미 존재하는 유저 정보가 반환") {
                result.member.id shouldBe memberId
                result.member.email shouldBe email
            }
        }

        When("신규 사용자로 로그인하는 경우") {
            val memberId = 2L
            val socialId = "2753426843"
            val email = "holeman80@nate.com"
            val newMember = createMember(memberId, email)

            prepareMock(newMember)
            every { mockSocialInfoRepository.save(any()) } returns SocialInfo.of(0L, socialId, email, SocialType.KAKAO, memberId)
            every { mockMemberCommandRepository.save(any()) } returns newMember

            val result = socialLoginService.login(createSocialLoginCommand(socialId, email, SocialType.KAKAO))

            Then("새로 가입된 유저 정보가 반환") {
                result.member.id shouldBe memberId
                result.member.email shouldBe email
            }
        }
    }
})

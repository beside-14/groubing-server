package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import com.beside.groubing.groubingserver.domain.member.domain.MemberType
import com.beside.groubing.groubingserver.domain.member.domain.SocialInfo
import com.beside.groubing.groubingserver.domain.member.domain.SocialInfoRepository
import com.beside.groubing.groubingserver.domain.member.domain.SocialType
import com.beside.groubing.groubingserver.domain.member.payload.command.SocialLoginCommand
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class SocialLoginServiceTest(
) : BehaviorSpec({
    val mockSocialInfoRepository = mockk<SocialInfoRepository>()
    val mockMemberRepository = mockk<MemberRepository>()
    val mockMemberFindDao = mockk<MemberFindDao>()
    val socialLoginService = SocialLoginService(mockSocialInfoRepository, mockMemberRepository, mockMemberFindDao)

    Given("SocialLoginService가 주어졌을 때") {
        fun prepareMock(existingMember: Member, existingSocialInfo: SocialInfo? = null) {
            every { mockMemberFindDao.findExistingMemberById(any()) } returns existingMember
            every { mockSocialInfoRepository.findByEmailAndSocialType(any(), any()) } returns Optional.ofNullable(existingSocialInfo)
        }

        fun createSocialLoginCommand(email: String, socialType: SocialType) = SocialLoginCommand(email, socialType)
        fun createMember(id: Long, email: String) = Member(id, email, "", "nickname", MemberRole.MEMBER, MemberType.SOCIAL)

        When("이미 존재하는 사용자로 로그인하는 경우") {
            val memberId = 1L
            val email = "email@example.com"
            val existingMember = createMember(memberId, email)
            val existingSocialInfo = SocialInfo(email, SocialType.KAKAO, memberId)

            prepareMock(existingMember, existingSocialInfo)
            val result = socialLoginService.login(createSocialLoginCommand(email, SocialType.KAKAO))

            Then("이미 존재하는 유저 정보가 반환") {
                result.id shouldBe memberId
                result.email shouldBe email
            }
        }

        When("신규 사용자로 로그인하는 경우") {
            val memberId = 2L
            val email = "holeman80@nate.com"
            val newMember = createMember(memberId, email)

            prepareMock(newMember)
            every { mockSocialInfoRepository.save(any()) } returns SocialInfo(email, SocialType.KAKAO, memberId)
            every { mockMemberRepository.save(any()) } returns newMember

            val result = socialLoginService.login(createSocialLoginCommand(email, SocialType.KAKAO))

            Then("새로 가입된 유저 정보가 반환") {
                result.id shouldBe memberId
                result.email shouldBe email
            }
        }
    }
})

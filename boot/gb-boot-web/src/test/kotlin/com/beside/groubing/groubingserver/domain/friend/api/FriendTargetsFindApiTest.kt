package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.domain.friend.application.FriendTargetsFindService
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import com.beside.groubing.groubingserver.domain.member.domain.MemberType
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@ApiTest
@WebMvcTest(controllers = [FriendTargetsFindApi::class])
class FriendTargetsFindApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val friendTargetsFindService: FriendTargetsFindService
) : BehaviorSpec({
    Given("유저가") {
        When("유저 검색을 위해 검색 창에 들어왔을 경우") {
            val members = (1..10).map {
                Member(
                    id = it.toLong(),
                    email = "groubing$it@daum.net",
                    password = "1234",
                    nickname = "groubing$it",
                    role = MemberRole.MEMBER,
                    memberType = MemberType.CLASSIC,
                    fcmToken = null,
                    notificationReceive = true,
                    active = true,
                    profileUrl = null
                )
            }
            every { friendTargetsFindService.findFriendTargets(1L) } returns members

            Then("전체 유저 목록을 nickname 오름차순으로 정렬하여 리턴한다.") {
                mockMvc.get("/api/friends/targets") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status {
                        isOk()
                    }
                }.andDocument(
                    "friend-target-find",
                    responseBody(
                        "[].memberId" responseType NUMBER means "유저 ID" example "1",
                        "[].email" responseType STRING means "유저 이메일" example "groubing1@daum.net",
                        "[].nickname" responseType STRING means "유저 닉네임" example "푸른바다123" formattedAs "^[가-힣a-zA-Z0-9]{2,7}",
                        "[].profileUrl" responseType STRING means "프로필 이미지 URL" example "/api/files/\${fileName} 혹은 null" isOptional true,
                    )
                )
            }
        }
    }
})

package com.beside.groubing.groubingserver.domain.feed.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.feed.application.FeedListFindService
import com.beside.groubing.groubingserver.domain.feed.application.FriendFeedListFindService
import com.beside.groubing.groubingserver.domain.feed.payload.response.FeedResponse
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import com.beside.groubing.groubingserver.domain.member.domain.MemberType
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FeedListFindApi::class)
@ApiTest
class FeedListFindApiTest(
    private val mockMvc: MockMvc,

    @MockkBean private val feedListFindService: FeedListFindService,

    @MockkBean private val friendFeedListFindService: FriendFeedListFindService,
) : BehaviorSpec({
    Given("FeedListFindApi가 주어졌을 때") {
        val memberId = 1L

        val healthTitles = (1..5).map { "운동하기$it" }
        val gameTitles = (1..5).map { "게임하기$it" }

        val feedResponses = listOf(
            FeedResponse.of(
                member = Member(
                    id = 2L,
                    email = "holeman79@nate.com",
                    password = "1234",
                    nickname = "홀맨친구",
                    role = MemberRole.MEMBER,
                    memberType = MemberType.CLASSIC,
                    fcmToken = null,
                    notificationReceive = true,
                    active = true,
                    profileUrl = null
                ),
                itemTitles = healthTitles
            ),
            FeedResponse.of(
                member = Member(
                    id = 3L,
                    email = "gather@naver.com",
                    password = "1234",
                    nickname = "슈뢰딩거",
                    role = MemberRole.MEMBER,
                    memberType = MemberType.CLASSIC,
                    fcmToken = null,
                    notificationReceive = true,
                    active = true,
                    profileUrl = null
                ),
                itemTitles = gameTitles
            )
        )

        every { feedListFindService.findAllFeeds(memberId) } returns feedResponses


        When("GET /api/feeds 요청이 들어왔을 때") {
            mockMvc.perform(
                get("/api/feeds")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(print())
                .andExpect(status().isOk)
                .andDocument(
                    "feed-list-find",
                    responseBody(
                        "[].memberId" responseType NUMBER means "Feed Member ID" example "1",
                        "[].nickname" responseType STRING means "유저 닉네임" example "그루빙멤버",
                        "[].profile" responseType STRING means "프로필 이미지 URL" isOptional true,
                        "[].feedItems[].title" responseType STRING means "빙고 Item Title" example "토익 만점 받기",
                        "[].isFriendRequestReceived" responseType BOOLEAN means "나에게 친구요청을 보낸 사람 여부" example true.toString(),
                        "[].isFriendRequestSend" responseType BOOLEAN means "내가 친구요청을 보낸 사람 여부" example false.toString()
                    )
                )
        }

        every { friendFeedListFindService.findFriendFeeds(memberId) } returns feedResponses

        When("GET /api/friend-feeds 요청이 들어왔을 때") {
            mockMvc.perform(
                get("/api/friend-feeds")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(print())
                .andExpect(status().isOk)
                .andDocument(
                    "friend-feed-list-find",
                    responseBody(
                        "[].memberId" responseType NUMBER means "Feed Member ID" example "1",
                        "[].nickname" responseType STRING means "유저 닉네임" example "그루빙멤버",
                        "[].profile" responseType STRING means "프로필 이미지 URL" isOptional true,
                        "[].feedItems[].title" responseType STRING means "빙고 Item Title" example "토익 만점 받기",
                        "[].isFriendRequestReceived" responseType BOOLEAN means "나에게 친구요청을 보낸 사람 여부" example true.toString(),
                        "[].isFriendRequestSend" responseType BOOLEAN means "내가 친구요청을 보낸 사람 여부" example false.toString()
                    )
                )
        }
    }
})

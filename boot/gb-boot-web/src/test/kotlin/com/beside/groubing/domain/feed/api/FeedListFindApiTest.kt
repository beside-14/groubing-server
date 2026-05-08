package com.beside.groubing.domain.feed.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.feed.application.FeedListFindService
import com.beside.groubing.domain.feed.application.FriendFeedListFindService
import com.beside.groubing.domain.feed.domain.FeedEntry
import com.beside.groubing.extension.getHttpHeaderJwt
import com.beside.groubing.vocabulary.feedItemTitle
import com.beside.groubing.vocabulary.isFriendRequestReceived
import com.beside.groubing.vocabulary.isFriendRequestSend
import com.beside.groubing.vocabulary.memberId
import com.beside.groubing.vocabulary.nickname
import com.beside.groubing.vocabulary.profileUrl
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

        val feedEntries = listOf(
            FeedEntry(
                memberId = 2L,
                nickname = "홀맨친구",
                profileUrl = null,
                itemTitles = (1..5).map { "운동하기$it" },
                isFriendRequestReceived = false,
                isFriendRequestSent = false
            ),
            FeedEntry(
                memberId = 3L,
                nickname = "슈뢰딩거",
                profileUrl = null,
                itemTitles = (1..5).map { "게임하기$it" },
                isFriendRequestReceived = false,
                isFriendRequestSent = false
            )
        )

        every { feedListFindService.findAllFeeds(memberId) } returns feedEntries

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
                        memberId("[].memberId", "Feed Member ID"),
                        nickname("[].nickname"),
                        profileUrl("[].profile"),
                        feedItemTitle(),
                        isFriendRequestReceived(),
                        isFriendRequestSend()
                    )
                )
        }

        every { friendFeedListFindService.findFriendFeeds(memberId) } returns feedEntries

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
                        memberId("[].memberId", "Feed Member ID"),
                        nickname("[].nickname"),
                        profileUrl("[].profile"),
                        feedItemTitle(),
                        isFriendRequestReceived(),
                        isFriendRequestSend()
                    )
                )
        }
    }
})

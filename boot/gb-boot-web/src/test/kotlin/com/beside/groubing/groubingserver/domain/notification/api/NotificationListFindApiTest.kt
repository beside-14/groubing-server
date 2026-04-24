package com.beside.groubing.groubingserver.domain.notification.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.notification.application.NotificationListFindService
import com.beside.groubing.groubingserver.domain.notification.domain.NotificationItem
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

@WebMvcTest(NotificationListFindApi::class)
@ApiTest
class NotificationListFindApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val notificationListFindService: NotificationListFindService
) : BehaviorSpec({
    Given("NotificationListFindApi가 주어졌을 때") {
        val memberId = 200L

        val notificationItems = listOf(
            NotificationItem(bingoBoardId = 1L, memberId = 100L,
                message = "holeman79님이 운동하기 빙고를 3 빙고 달성했어요!", profileFileName = null),
            NotificationItem(bingoBoardId = 2L, memberId = 101L,
                message = "awaji님이 이직 준비하기 빙고의 목표 빙고 수를 달성했어요!", profileFileName = "file/profile/profile1.jpg"),
            NotificationItem(bingoBoardId = 3L, memberId = 102L,
                message = "푸른바다님이 코딩 공부하기 빙고에서 달성한 빙고 중 스프링 api 강의듣기 빙고 아이템을 취소했어요.", profileFileName = "file/profile/profile2.jpg")
        )

        every { notificationListFindService.findNotifications(memberId) } returns notificationItems

        When("GET /api/notifications 요청이 들어왔을 때") {
            mockMvc.perform(
                get("/api/notifications")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("page", "0")
                    .param("size", "20")
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(print())
                .andExpect(status().isOk)
                .andDocument(
                    "notification-list-find",
                    requestParam(
                        "page" requestParam "페이지 수" example "1,2,3..",
                        "size" requestParam "페이지 Element 수" example "20"
                    ),
                    responseBody(
                        "[].bingoBoardId" responseType NUMBER means "빙고보드 ID" example "1",
                        "[].memberId" responseType NUMBER means "알림 메세지 생성 member id" example "100",
                        "[].message" responseType STRING means "알림 메세지" example "awaji님이 이직 준비하기 빙고의 목표 빙고 수를 달성했어요!",
                        "[].profileUrl" responseType STRING means "프로필 이미지 URL" isOptional true,
                    )
                )
        }
    }
})

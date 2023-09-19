package com.beside.groubing.groubingserver.domain.notification.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.notification.application.NotificationListFindService
import com.beside.groubing.groubingserver.domain.notification.domain.Notification
import com.beside.groubing.groubingserver.domain.notification.payload.response.NotificationResponse
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.beside.groubing.groubingserver.global.response.PageResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
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

        val notificationResponses = listOf(
            NotificationResponse.create(
                Notification(bingoBoardId = 1L, memberId = 100L, message = "holeman79님이 운동하기 빙고를 3 빙고 달성했어요!")
            ),
            NotificationResponse.create(
                Notification(bingoBoardId = 2L, memberId = 101L, message = "awaji님이 이직 준비하기 빙고의 목표 빙고 수를 달성했어요!")
            ),
            NotificationResponse.create(
                Notification(bingoBoardId = 3L, memberId = 102L, message = "푸른바다님이 코딩 공부하기 빙고에서 달성한 빙고 중 스프링 api 강의듣기 빙고 아이템을 취소했어요.")
            )
        )

        every { notificationListFindService.findNotifications(memberId, PageRequest.of(0, 20)) } returns PageResponse(PageImpl(notificationResponses))

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
                        "contents[].bingoBoardId" responseType NUMBER means "빙고보드 ID" example "1",
                        "contents[].memberId" responseType NUMBER means "알림 메세지 생성 member id" example "100",
                        "contents[].message" responseType STRING means "알림 메세지" example "awaji님이 이직 준비하기 빙고의 목표 빙고 수를 달성했어요!",
                        "totalElements" responseType NUMBER means "전체 알림 개수" example "100",
                        "numberOfElements" responseType NUMBER means "현재 페이지 알림 개수" example "20",
                        "currentPage" responseType NUMBER means "현재 페이지 번호" example "1",
                        "totalPages" responseType NUMBER means "총 페이지 개수" example "5",
                        "size" responseType NUMBER means "요청 페이지 개수" example "20",
                        "first" responseType BOOLEAN means "첫 페이지 여부" example "true",
                        "last" responseType BOOLEAN means "마지막 페이지 여부" example "false"
                    )
                )
        }
    }
})

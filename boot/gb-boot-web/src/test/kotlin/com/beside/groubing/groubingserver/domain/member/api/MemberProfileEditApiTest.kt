package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.requestPart
import com.beside.groubing.groubingserver.docs.requestParts
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.member.application.MemberProfileEditService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberProfileResponse
import com.beside.groubing.groubingserver.extension.multipart
import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@ApiTest
@WebMvcTest(controllers = [MemberProfileEditApi::class])
class MemberProfileEditApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val memberProfileEditService: MemberProfileEditService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val imageData = "Test image data".toByteArray()
        val imageResource = InputStreamResource(ByteArrayInputStream(imageData))
        val profile = MockMultipartFile("profile", "test.jpg", MediaType.IMAGE_JPEG_VALUE, imageResource.inputStream)
        val fileName = "${LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}-${UUID.randomUUID()}.jpg"
        val fileInfo = FileInfo.create("/groubing", fileName, profile.originalFilename)

        When("새로운 프로필 이미지를 등록하는 경우") {
            every { memberProfileEditService.edit(any(), any()) } returns fileInfo.url
            val response = MemberProfileResponse(fileInfo.url)

            Then("프로필 이미지 URL 을 응답하도록 한다.") {
                mockMvc.perform(
                    multipart(HttpMethod.PATCH, "/api/members/{id}/profile", id)
                        .file(profile)
                ).andDo(print())
                    .andExpect(status().isOk)
                    .andExpect(content().json(mapper.writeValueAsString(ApiResponse.OK(response))))
                    .andDocument(
                        "member-profile-edit",
                        pathVariables(
                            "id" requestParam "유저 ID" example id.toString()
                        ),
                        requestParts(
                            "profile" requestPart "프로필 이미지 파일" formattedAs ".png / .jpeg / .jpg"
                        ),
                        responseBody(
                            "profileUrl" responseType STRING means "프로필 이미지 URL" example fileInfo.url
                        )
                    )
            }
        }
    }
})

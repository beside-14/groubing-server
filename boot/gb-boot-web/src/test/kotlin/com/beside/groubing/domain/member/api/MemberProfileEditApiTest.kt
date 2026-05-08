package com.beside.groubing.domain.member.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.pathVariables
import com.beside.groubing.docs.requestPart
import com.beside.groubing.docs.requestParts
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.member.application.MemberProfileEditService
import com.beside.groubing.vocabulary.memberIdPath
import com.beside.groubing.vocabulary.profileUrl
import com.beside.groubing.domain.member.payload.response.MemberProfileResponse
import com.beside.groubing.extension.multipart
import com.beside.groubing.global.domain.file.application.FileProvider
import com.beside.groubing.global.domain.file.domain.FileInfo
import com.beside.groubing.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
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
    beforeSpec { mockkObject(FileProvider.Companion) }
    afterSpec { unmockkObject(FileProvider.Companion) }

    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val imageData = "Test image data".toByteArray()
        val imageResource = InputStreamResource(ByteArrayInputStream(imageData))
        val profile = MockMultipartFile("profile", "test.jpg", MediaType.IMAGE_JPEG_VALUE, imageResource.inputStream)
        val fileName = "${LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}-${UUID.randomUUID()}.jpg"
        val fileInfo = FileInfo.create("/groubing", fileName, profile.originalFilename)

        When("새로운 프로필 이미지를 등록하는 경우") {
            every { FileProvider.upload(any()) } returns fileInfo
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
                            memberIdPath() example id.toString()
                        ),
                        requestParts(
                            "profile" requestPart "프로필 이미지 파일" formattedAs ".png / .jpeg / .jpg"
                        ),
                        responseBody(
                            profileUrl() example fileInfo.url
                        )
                    )
            }
        }
    }
})

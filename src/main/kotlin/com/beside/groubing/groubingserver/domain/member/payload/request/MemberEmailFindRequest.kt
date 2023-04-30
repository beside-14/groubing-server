package com.beside.groubing.groubingserver.domain.member.payload.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class MemberEmailFindRequest(
    @field:Email(message = "이메일을 올바른 양식으로 입력해 주세요.")
    @field:NotBlank(message = "이메일을 입력해 주세요")
    val email: String
)

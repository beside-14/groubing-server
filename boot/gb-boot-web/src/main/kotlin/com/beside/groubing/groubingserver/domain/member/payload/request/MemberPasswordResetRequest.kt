package com.beside.groubing.groubingserver.domain.member.payload.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class MemberPasswordResetRequest(
    @field:NotBlank(message = "이전 비밀번호를 입력해 주세요.")
    val beforePassword: String,

    @field:NotBlank(message = "변경할 비밀번호를 입력해 주세요.")
    @field:Length(min = 8, max = 20, message = "비밀번호는 8 ~ 20자 내외로 입력해 주세요.")
    @field:Pattern(regexp = "^[a-zA-Z0-9!-/:-@\\[-_~]{8,20}", message = "허용하지 않는 특수문자가 포함되어 있어요.")
    val afterPassword: String
)

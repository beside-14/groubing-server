package com.beside.groubing.groubingserver.domain.member.payload.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class MemberNicknameEditRequest(
    @field:NotBlank(message = "닉네임을 입력해 주세요.")
    @field:Length(min = 2, max = 7, message = "닉네임은 2 ~ 7자 내외로 입력해 주세요.")
    @field:Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,7}", message = "허용하지 않는 특수문자가 포함되어 있어요.")
    val nickname: String
)

package com.beside.groubing.groubingserver.domain.member.payload.request

import com.beside.groubing.groubingserver.domain.member.domain.SocialType
import com.beside.groubing.groubingserver.domain.member.payload.command.SocialLoginCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SocialLoginRequest(
    @field:Email(message = "이메일을 올바른 양식으로 입력해 주세요.")
    @field:NotBlank
    val email: String,

    @field:NotNull(message = "소셜 타입을 전달해 주세요.")
    val socialType: SocialType
) {
    fun command() = SocialLoginCommand(email, socialType)
}

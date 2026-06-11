package com.beside.groubing.domain.member.payload.request

import com.beside.groubing.domain.auth.application.command.LoginCommand
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "아이디를 입력해 주세요.")
    val loginId: String,

    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String,

    val fcmToken: String?
) {
    fun command() = LoginCommand(loginId, password, fcmToken)
}

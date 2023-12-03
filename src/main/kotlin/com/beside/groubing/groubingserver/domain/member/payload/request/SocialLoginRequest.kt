package com.beside.groubing.groubingserver.domain.member.payload.request

import com.beside.groubing.groubingserver.domain.member.domain.SocialType
import com.beside.groubing.groubingserver.domain.member.payload.command.SocialLoginCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SocialLoginRequest(
    @field:NotBlank
    val id: String,

    val email: String?,

    @field:NotNull(message = "소셜 타입을 전달해 주세요.")
    val socialType: SocialType
) {
    fun command() = SocialLoginCommand(id, email, socialType)
}

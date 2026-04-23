package com.beside.groubing.groubingserver.domain.friend.payload.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class FriendAddRequest(
    @field:NotNull(message = "회원 아이디를 입력해 주세요.")
    @field:Min(value = 1L, message = "유효하지 않은 회원 아이디입니다.")
    val inviteeId: Long
)

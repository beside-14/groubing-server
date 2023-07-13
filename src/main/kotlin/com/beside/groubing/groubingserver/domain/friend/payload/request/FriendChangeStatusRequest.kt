package com.beside.groubing.groubingserver.domain.friend.payload.request

import jakarta.validation.constraints.NotNull

data class FriendChangeStatusRequest(
    @field:NotNull(message = "요청 수락 여부를 설정해 주세요.")
    val accept: Boolean
)

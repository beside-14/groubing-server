package com.beside.groubing.groubingserver.domain.member.payload.request

import jakarta.validation.constraints.NotNull

data class MemberFriendChangeStatusRequest(
    @field:NotNull(message = "요청 수락 여부를 설정해 주세요.")
    val accept: Boolean
)

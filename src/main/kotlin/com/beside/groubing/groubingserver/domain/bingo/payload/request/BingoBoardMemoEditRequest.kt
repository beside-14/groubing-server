package com.beside.groubing.groubingserver.domain.bingo.payload.request

import org.hibernate.validator.constraints.Length

class BingoBoardMemoEditRequest(
    val id: Long,
    @Length(max = 200, message = "빙고 메모는 200자 이내로 입력해 주세요.")
    val memo: String?
)

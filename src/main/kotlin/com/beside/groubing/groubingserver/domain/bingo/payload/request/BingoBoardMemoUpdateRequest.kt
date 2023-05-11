package com.beside.groubing.groubingserver.domain.bingo.payload.request

import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardMemoUpdateCommand
import org.hibernate.validator.constraints.Length

class BingoBoardMemoUpdateRequest(
    @field:Length(max = 200, message = "빙고 메모는 200자 이내로 입력해 주세요.")
    val memo: String?
) {
    fun command(): BingoBoardMemoUpdateCommand {
        return BingoBoardMemoUpdateCommand.createCommand(memo)
    }
}

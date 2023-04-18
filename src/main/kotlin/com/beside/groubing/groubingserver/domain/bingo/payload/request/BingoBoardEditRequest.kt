package com.beside.groubing.groubingserver.domain.bingo.payload.request

import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardEditCommand
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

class BingoBoardEditRequest(
    val id: Long,
    @NotBlank(message = "제목을 입력해 주세요.")
    @Length(min = 1, max = 40, message = "제목은 40자 이내로 입력해 주세요.")
    val title: String?,
    val goal: Int?
) {
    fun command(memberId: Long): BingoBoardEditCommand =
        BingoBoardEditCommand.createCommand(memberId, id, title, goal)
}

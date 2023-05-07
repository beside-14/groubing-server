package com.beside.groubing.groubingserver.domain.bingo.payload.request

import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoItemUpdateCommand
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

class BingoItemUpdateRequest(
    @NotBlank(message = "제목을 입력해 주세요.")
    @Length(min = 2, max = 25, message = "제목은 25자 이내로 입력해 주세요.")
    val title: String,
    @NotBlank(message = "내용을 입력해 주세요.")
    @Length(min = 2, max = 25, message = "내용은 25자 이내로 입력해 주세요.")
    val subTitle: String?
) {
    fun command(): BingoItemUpdateCommand =
        BingoItemUpdateCommand.createCommand(title, subTitle)
}

package com.beside.groubing.domain.bingo.payload.request

import com.beside.groubing.domain.bingo.payload.command.BingoItemUpdateCommand
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

class BingoItemUpdateRequest(
    @field:NotBlank(message = "제목을 입력해 주세요.")
    @field:Length(min = 2, max = 25, message = "제목은 2자~25자 이내로 입력해 주세요.")
    val title: String,

    val subTitle: String?
) {
    fun command(): BingoItemUpdateCommand =
        BingoItemUpdateCommand.of(title, subTitle)
}

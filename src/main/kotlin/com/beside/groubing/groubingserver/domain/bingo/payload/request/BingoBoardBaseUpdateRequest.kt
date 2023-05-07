package com.beside.groubing.groubingserver.domain.bingo.payload.request

import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardBaseUpdateCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

class BingoBoardBaseUpdateRequest(
    @NotBlank(message = "제목을 입력해 주세요.")
    @Pattern(regexp = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}", message = "허용하지 않는 특수문자가 포함되어 있어요.")
    @Length(min = 1, max = 40, message = "제목은 40자 이내로 입력해 주세요.")
    val title: String,
    val goal: Int
) {
    fun command(): BingoBoardBaseUpdateCommand {
        return BingoBoardBaseUpdateCommand.createCommand(title, goal)
    }
}


package com.beside.groubing.groubingserver.domain.bingo.payload.request

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardCreateCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class BingoBoardCreateRequest(
    @field:NotBlank(message = "제목을 입력해 주세요.")
    @field:Pattern(regexp = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}", message = "허용하지 않는 특수문자가 포함되어 있어요.")
    @field:Length(min = 1, max = 40, message = "제목은 40자 이내로 입력해 주세요.")
    val title: String,
    val goal: Int,
    val boardType: BingoBoardType,
    val open: Boolean,
    val bingoSize: Int
) {
    fun command(memberId: Long): BingoBoardCreateCommand =
        BingoBoardCreateCommand.createCommand(memberId, title, goal, boardType, open, bingoSize)
}

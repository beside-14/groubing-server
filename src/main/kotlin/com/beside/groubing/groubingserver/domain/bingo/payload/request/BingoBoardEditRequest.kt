package com.beside.groubing.groubingserver.domain.bingo.payload.request

import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardEditCommand
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import java.time.LocalDate

class BingoBoardEditRequest(
    val id: Long,
    @NotBlank(message = "제목을 입력해 주세요.")
    @Length(min = 1, max = 40, message = "제목은 40자 이내로 입력해 주세요.")
    val title: String?,
    val goal: Int?,
    @FutureOrPresent(message = "시작일은 현재보다 과거로 설정 할 수 없어요.")
    val since: LocalDate?,
    @Future(message = "종료일은 현재보다 과거로 설정 할 수 없어요.")
    val until: LocalDate?,
    @Length(min = 1, max = 40, message = "메모는 40자 이내로 입력해 주세요.")
    val memo: String?
) {
    fun command(memberId: Long): BingoBoardEditCommand =
        BingoBoardEditCommand.createCommand(memberId, id, title, goal, since, until, memo)
}

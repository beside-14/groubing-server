package com.beside.groubing.groubingserver.domain.bingo.payload.request

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardCreateCommand
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import java.time.LocalDate

data class BingoBoardCreateRequest(
    @NotBlank(message = "제목을 입력해 주세요.")
    @Length(message = "제목은 40자 이내로 입력해 주세요.")
    val title: String,
    val goal: Int,
    val boardType: BingoBoardType,
    val open: Boolean,
    @FutureOrPresent(message = "시작일은 현재보다 과거로 설정 할 수 없어요.")
    val since: LocalDate,
    @Future(message = "종료일은 현재보다 과거로 설정 할 수 없어요.")
    val until: LocalDate,
    val bingoSize: Int
) {
    fun command(memberId: Long): BingoBoardCreateCommand =
        BingoBoardCreateCommand.createCommand(memberId, title, goal, boardType, open, since, until, bingoSize)
}

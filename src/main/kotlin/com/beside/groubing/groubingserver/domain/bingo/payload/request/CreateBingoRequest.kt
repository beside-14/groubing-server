package com.beside.groubing.groubingserver.domain.bingo.payload.request

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoColor
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoType
import com.beside.groubing.groubingserver.domain.bingo.payload.command.CreateBingoCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.validate.NineGroup
import com.beside.groubing.groubingserver.domain.bingo.payload.validate.SixteenGroup
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Range
import java.time.LocalDate

data class CreateBingoRequest(
    @NotBlank(message = "빙고 제목을 입력해 주세요.")
    @Size(min = 1, max = 40, message = "빙고 제목은 40자 이내로 입력해 주세요.")
    @Pattern(regexp = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}", message = "")
    val title: String,

    val type: BingoType,

    val size: BingoSize,

    val color: BingoColor,

    @Range(min = 1, max = 3, groups = [NineGroup::class], message = "3x3 사이즈의 목표 빙고 개수는 최대 3개까지 설정 가능 합니다.")
    @Range(min = 1, max = 4, groups = [SixteenGroup::class], message = "4x4 사이즈의 목표 빙고 개수는 최대 4개까지 설정 가능 합니다.")
    val goal: Int,

    val open: Boolean,

    @Future(message = "빙고 시작일은 현재보다 과거에 있을 수 없습니다.")
    val since: LocalDate,

    @Future(message = "빙고 종료일은 현재보다 과거에 있을 수 없습니다.")
    val until: LocalDate
) {
    fun command(memberId: Long): CreateBingoCommand =
        CreateBingoCommand(memberId, title, type, size, color, goal, open, since, until);
}

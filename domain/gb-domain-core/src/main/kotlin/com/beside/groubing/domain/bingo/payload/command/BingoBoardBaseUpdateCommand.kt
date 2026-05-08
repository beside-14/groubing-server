package com.beside.groubing.domain.bingo.payload.command

import com.beside.groubing.domain.bingo.domain.BingoBoard
import java.time.LocalDate

class BingoBoardBaseUpdateCommand private constructor(
    val title: String,
    val goal: Int,
    val since: LocalDate,
    val until: LocalDate
) {
    fun update(bingoBoard: BingoBoard, memberId: Long) {
        bingoBoard.updateBase(memberId, title, goal, since, until)
    }

    companion object {
        fun of(
            title: String,
            goal: Int,
            since: LocalDate,
            until: LocalDate
        ): BingoBoardBaseUpdateCommand =
            BingoBoardBaseUpdateCommand(title, goal, since, until)
    }
}

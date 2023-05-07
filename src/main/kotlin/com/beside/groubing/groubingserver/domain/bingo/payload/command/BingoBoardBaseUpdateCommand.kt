package com.beside.groubing.groubingserver.domain.bingo.payload.command

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
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
        fun createCommand(
            title: String,
            goal: Int,
            since: LocalDate,
            until: LocalDate
        ): BingoBoardBaseUpdateCommand =
            BingoBoardBaseUpdateCommand(title, goal, since, until)
    }
}

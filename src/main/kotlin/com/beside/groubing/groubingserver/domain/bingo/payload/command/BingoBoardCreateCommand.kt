package com.beside.groubing.groubingserver.domain.bingo.payload.command

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import java.time.LocalDate

class BingoBoardCreateCommand private constructor(
    val memberId: Long,
    val title: String,
    val goal: Int,
    val boardType: BingoBoardType,
    val open: Boolean,
    val since: LocalDate,
    val until: LocalDate,
    val bingoSize: Int
) {
    fun toNewBingoBoard(): BingoBoard =
        BingoBoard.createBingoBoard(memberId, title, goal, boardType, open, since, until, bingoSize)

    companion object {
        fun createCommand(
            memberId: Long,
            title: String,
            goal: Int,
            boardType: BingoBoardType,
            open: Boolean,
            since: LocalDate,
            until: LocalDate,
            bingoSize: Int
        ): BingoBoardCreateCommand =
            BingoBoardCreateCommand(memberId, title, goal, boardType, open, since, until, bingoSize)
    }
}

package com.beside.groubing.groubingserver.domain.bingo.payload.command

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import java.time.LocalDate

data class BingoBoardCreateCommand(
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
}

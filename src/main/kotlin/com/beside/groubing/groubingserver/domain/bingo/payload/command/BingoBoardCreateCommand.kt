package com.beside.groubing.groubingserver.domain.bingo.payload.command

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType

class BingoBoardCreateCommand internal constructor(
    val memberId: Long,
    val title: String,
    val goal: Int,
    val boardType: BingoBoardType,
    val open: Boolean,
    val bingoSize: Int
) {
    fun toNewBingoBoard(): BingoBoard =
        BingoBoard.create(memberId, title, goal, boardType, open, bingoSize)

    companion object {
        fun createCommand(
            memberId: Long,
            title: String,
            goal: Int,
            boardType: BingoBoardType,
            open: Boolean,
            bingoSize: Int
        ): BingoBoardCreateCommand =
            BingoBoardCreateCommand(memberId, title, goal, boardType, open, bingoSize)
    }
}

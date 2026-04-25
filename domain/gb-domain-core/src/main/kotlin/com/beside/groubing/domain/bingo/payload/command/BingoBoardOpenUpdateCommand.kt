package com.beside.groubing.domain.bingo.payload.command

import com.beside.groubing.domain.bingo.domain.BingoBoard

class BingoBoardOpenUpdateCommand private constructor(
    private val open: Boolean
) {
    fun update(bingoBoard: BingoBoard, memberId: Long) {
        bingoBoard.updateBingoOpen(memberId, open)
    }

    companion object {
        fun of(
            open: Boolean
        ): BingoBoardOpenUpdateCommand {
            return BingoBoardOpenUpdateCommand(open)
        }
    }
}

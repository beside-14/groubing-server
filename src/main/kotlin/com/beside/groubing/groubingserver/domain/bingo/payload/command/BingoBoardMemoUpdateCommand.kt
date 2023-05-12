package com.beside.groubing.groubingserver.domain.bingo.payload.command

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard

class BingoBoardMemoUpdateCommand private constructor(
    private val memo: String?
) {
    fun update(bingoBoard: BingoBoard, memberId: Long) {
        bingoBoard.updateBingoMemo(memberId, memo)
    }

    companion object {
        fun createCommand(
            memo: String?
        ): BingoBoardMemoUpdateCommand {
            return BingoBoardMemoUpdateCommand(memo)
        }
    }
}

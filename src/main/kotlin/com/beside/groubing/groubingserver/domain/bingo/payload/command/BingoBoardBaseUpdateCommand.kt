package com.beside.groubing.groubingserver.domain.bingo.payload.command

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard

class BingoBoardBaseUpdateCommand private constructor(
    val title: String,
    val goal: Int
) {
    fun update(bingoBoard: BingoBoard, memberId: Long) {
        bingoBoard.updateBase(memberId, title, goal)
    }

    companion object {
        fun createCommand(
            title: String,
            goal: Int
        ): BingoBoardBaseUpdateCommand =
            BingoBoardBaseUpdateCommand(title, goal)
    }
}

package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoBoard
import java.time.LocalDate

class BingoBoardBaseUpdateResponse private constructor(
    val id: Long,
    val title: String,
    val goal: Int,
    val since: LocalDate?,
    val until: LocalDate?
) {
    companion object {
        fun fromBingoBoard(board: BingoBoard): BingoBoardBaseUpdateResponse =
            BingoBoardBaseUpdateResponse(
                id = board.id,
                title = board.title,
                goal = board.goal,
                since = board.since,
                until = board.until
            )
    }
}

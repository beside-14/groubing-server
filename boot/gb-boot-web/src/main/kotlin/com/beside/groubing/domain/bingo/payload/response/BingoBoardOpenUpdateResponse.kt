package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoBoard

class BingoBoardOpenUpdateResponse private constructor(
    val id: Long,
    val open: Boolean
) {
    companion object {
        fun fromBingoBoard(board: BingoBoard): BingoBoardOpenUpdateResponse =
            BingoBoardOpenUpdateResponse(id = board.id, open = board.open)
    }
}

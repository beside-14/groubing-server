package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoBoard

class BingoBoardMemoUpdateResponse private constructor(
    val id: Long,
    val memo: String?
) {
    companion object {
        fun fromBingoBoard(board: BingoBoard): BingoBoardMemoUpdateResponse =
            BingoBoardMemoUpdateResponse(id = board.id, memo = board.memo)
    }
}

package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

class BingoBoardMemoUpdateResponse private constructor(
    @EncryptId(ObfuscationType.BINGO_BOARD)
    val id: Long,
    val memo: String?
) {
    companion object {
        fun fromBingoBoard(board: BingoBoard): BingoBoardMemoUpdateResponse =
            BingoBoardMemoUpdateResponse(id = board.id, memo = board.memo)
    }
}

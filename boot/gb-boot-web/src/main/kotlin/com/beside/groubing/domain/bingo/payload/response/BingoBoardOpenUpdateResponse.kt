package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

class BingoBoardOpenUpdateResponse private constructor(
    @EncryptId(ObfuscationType.BINGO_BOARD)
    val id: Long,
    val open: Boolean
) {
    companion object {
        fun fromBingoBoard(board: BingoBoard): BingoBoardOpenUpdateResponse =
            BingoBoardOpenUpdateResponse(id = board.id, open = board.open)
    }
}

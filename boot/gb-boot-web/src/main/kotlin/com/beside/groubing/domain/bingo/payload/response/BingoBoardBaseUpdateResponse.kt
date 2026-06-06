package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId
import java.time.LocalDate

class BingoBoardBaseUpdateResponse private constructor(
    @EncryptId(ObfuscationType.BINGO_BOARD)
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

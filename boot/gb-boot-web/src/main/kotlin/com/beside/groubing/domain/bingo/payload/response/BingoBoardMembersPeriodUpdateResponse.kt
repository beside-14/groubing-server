package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId
import java.time.LocalDate

class BingoBoardMembersPeriodUpdateResponse private constructor(
    @EncryptId(ObfuscationType.BINGO_BOARD)
    val id: Long,
    val bingoMembers: List<Long>,
    val since: LocalDate?,
    val until: LocalDate?
) {
    companion object {
        fun fromBingoBoard(board: BingoBoard): BingoBoardMembersPeriodUpdateResponse =
            BingoBoardMembersPeriodUpdateResponse(
                id = board.id,
                bingoMembers = board.bingoMembers.memberIds(),
                since = board.since,
                until = board.until
            )
    }
}

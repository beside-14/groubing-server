package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId
import java.time.LocalDate

class BingoBoardMembersPeriodUpdateResponse private constructor(
    @EncryptId(ObfuscationType.BINGO_BOARD)
    val id: Long,
    @EncryptId(ObfuscationType.MEMBER)
    val bingoMemberIds: List<Long>,
    val since: LocalDate?,
    val until: LocalDate?
) {
    companion object {
        fun fromBingoBoard(board: BingoBoard): BingoBoardMembersPeriodUpdateResponse =
            BingoBoardMembersPeriodUpdateResponse(
                id = board.id,
                bingoMemberIds = board.bingoMembers.memberIds(),
                since = board.since,
                until = board.until
            )
    }
}

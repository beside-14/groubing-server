package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoBoard
import java.time.LocalDate

class BingoBoardMembersPeriodUpdateResponse private constructor(
    val id: Long,
    val bingoMembers: List<Long>,
    val since: LocalDate?,
    val until: LocalDate?
) {
    companion object {
        fun fromBingoBoard(board: BingoBoard): BingoBoardMembersPeriodUpdateResponse =
            BingoBoardMembersPeriodUpdateResponse(
                id = board.id,
                bingoMembers = board.bingoMembers.map { it.memberId },
                since = board.since,
                until = board.until
            )
    }
}

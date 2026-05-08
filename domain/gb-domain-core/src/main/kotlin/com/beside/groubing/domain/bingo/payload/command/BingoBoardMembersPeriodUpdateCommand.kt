package com.beside.groubing.domain.bingo.payload.command

import com.beside.groubing.domain.bingo.domain.BingoBoard
import java.time.LocalDate

class BingoBoardMembersPeriodUpdateCommand private constructor(
    val bingoMembers: List<Long>,
    private val since: LocalDate,
    private val until: LocalDate
) {
    fun update(bingoBoard: BingoBoard, memberId: Long) {
        bingoBoard.updateBingoMembersPeriod(memberId, bingoMembers, since, until)
    }

    companion object {
        fun of(
            bingoMembers: List<Long>,
            since: LocalDate,
            until: LocalDate
        ): BingoBoardMembersPeriodUpdateCommand {
            return BingoBoardMembersPeriodUpdateCommand(bingoMembers, since, until)
        }
    }

}

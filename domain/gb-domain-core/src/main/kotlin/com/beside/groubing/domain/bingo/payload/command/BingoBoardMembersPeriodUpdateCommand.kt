package com.beside.groubing.domain.bingo.payload.command

import java.time.LocalDate

class BingoBoardMembersPeriodUpdateCommand private constructor(
    val bingoMembers: List<Long>,
    val since: LocalDate,
    val until: LocalDate
) {
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

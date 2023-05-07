package com.beside.groubing.groubingserver.domain.bingo.payload.request

import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardMembersPeriodUpdateCommand
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDate

class BingoBoardMembersPeriodUpdateRequest(
    @NotEmpty
    val bingoMembers: List<Long>,

    val since: LocalDate,

    @field:Future
    val until: LocalDate
) {
    fun command(): BingoBoardMembersPeriodUpdateCommand {
        return BingoBoardMembersPeriodUpdateCommand.createCommand(bingoMembers, since, until)
    }
}

package com.beside.groubing.domain.bingo.payload.request

import com.beside.groubing.domain.bingo.payload.command.BingoBoardMembersPeriodUpdateCommand
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDate

class BingoBoardMembersPeriodUpdateRequest(
    @field:NotEmpty
    @field:EncryptId(ObfuscationType.MEMBER)
    val bingoMembers: List<Long>,

    val since: LocalDate,

    @field:Future
    val until: LocalDate
) {
    fun command(): BingoBoardMembersPeriodUpdateCommand {
        return BingoBoardMembersPeriodUpdateCommand.of(bingoMembers, since, until)
    }
}

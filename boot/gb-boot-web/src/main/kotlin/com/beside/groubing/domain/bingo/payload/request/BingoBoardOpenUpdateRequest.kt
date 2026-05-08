package com.beside.groubing.domain.bingo.payload.request

import com.beside.groubing.domain.bingo.payload.command.BingoBoardOpenUpdateCommand

class BingoBoardOpenUpdateRequest(
    val open: Boolean
) {
    fun command(): BingoBoardOpenUpdateCommand {
        return BingoBoardOpenUpdateCommand.of(open)
    }
}

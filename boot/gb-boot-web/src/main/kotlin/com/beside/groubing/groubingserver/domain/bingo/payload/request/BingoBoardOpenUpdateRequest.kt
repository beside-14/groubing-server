package com.beside.groubing.groubingserver.domain.bingo.payload.request

import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardOpenUpdateCommand

class BingoBoardOpenUpdateRequest(
    val open: Boolean
) {
    fun command(): BingoBoardOpenUpdateCommand {
        return BingoBoardOpenUpdateCommand.createCommand(open)
    }
}

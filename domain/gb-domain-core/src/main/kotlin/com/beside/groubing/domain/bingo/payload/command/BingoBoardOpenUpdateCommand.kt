package com.beside.groubing.domain.bingo.payload.command

class BingoBoardOpenUpdateCommand private constructor(
    val open: Boolean
) {
    companion object {
        fun of(
            open: Boolean
        ): BingoBoardOpenUpdateCommand {
            return BingoBoardOpenUpdateCommand(open)
        }
    }
}

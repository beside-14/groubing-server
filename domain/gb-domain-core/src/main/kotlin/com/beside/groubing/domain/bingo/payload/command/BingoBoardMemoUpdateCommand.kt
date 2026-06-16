package com.beside.groubing.domain.bingo.payload.command

class BingoBoardMemoUpdateCommand private constructor(
    val memo: String?
) {
    companion object {
        fun of(
            memo: String?
        ): BingoBoardMemoUpdateCommand {
            return BingoBoardMemoUpdateCommand(memo)
        }
    }
}

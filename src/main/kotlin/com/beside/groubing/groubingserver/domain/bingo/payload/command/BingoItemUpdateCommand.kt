package com.beside.groubing.groubingserver.domain.bingo.payload.command

class BingoItemUpdateCommand private constructor(
    val title: String,
    val subTitle: String?
) {
    companion object {
        fun createCommand(
            title: String,
            subTitle: String?
        ): BingoItemUpdateCommand =
            BingoItemUpdateCommand(title, subTitle)
    }
}

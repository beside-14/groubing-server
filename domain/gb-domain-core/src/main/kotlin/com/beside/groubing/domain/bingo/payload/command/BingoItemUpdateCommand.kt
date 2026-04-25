package com.beside.groubing.domain.bingo.payload.command

class BingoItemUpdateCommand private constructor(
    val title: String,
    val subTitle: String?
) {
    companion object {
        fun of(
            title: String,
            subTitle: String?
        ): BingoItemUpdateCommand =
            BingoItemUpdateCommand(title, subTitle)
    }
}

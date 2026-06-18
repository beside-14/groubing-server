package com.beside.groubing.domain.bingo.payload.command

import java.time.LocalDate

class BingoBoardBaseUpdateCommand private constructor(
    val title: String,
    val goal: Int,
    val since: LocalDate,
    val until: LocalDate
) {
    companion object {
        fun of(
            title: String,
            goal: Int,
            since: LocalDate,
            until: LocalDate
        ): BingoBoardBaseUpdateCommand =
            BingoBoardBaseUpdateCommand(title, goal, since, until)
    }
}

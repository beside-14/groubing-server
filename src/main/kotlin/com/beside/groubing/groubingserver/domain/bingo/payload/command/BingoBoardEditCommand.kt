package com.beside.groubing.groubingserver.domain.bingo.payload.command

import java.time.LocalDate

class BingoBoardEditCommand private constructor(
    val memberId: Long,
    val id: Long,
    val title: String,
    val goal: Int,
    val since: LocalDate,
    val until: LocalDate
) {

    companion object {
        fun createCommand(
            memberId: Long,
            id: Long,
            title: String,
            goal: Int,
            since: LocalDate,
            until: LocalDate
        ): BingoBoardEditCommand =
            BingoBoardEditCommand(memberId, id, title, goal, since, until)
    }
}

package com.beside.groubing.groubingserver.domain.bingo.payload.command

class BingoBoardEditCommand private constructor(
    val memberId: Long,
    val id: Long,
    val title: String?,
    val goal: Int?
) {

    companion object {
        fun createCommand(
            memberId: Long,
            id: Long,
            title: String?,
            goal: Int?
        ): BingoBoardEditCommand =
            BingoBoardEditCommand(memberId, id, title, goal)
    }
}

package com.beside.groubing.groubingserver.domain.bingo.payload.command

class BingoItemEditCommand private constructor(
    val id: Long,
    val bingoBoardId: Long,
    val memberId: Long,
    val title: String,
    val subTitle: String?,
    val imageUrl: String?
) {
    companion object {
        fun createCommand(
            id: Long,
            bingoBoardId: Long,
            memberId: Long,
            title: String,
            subTitle: String?,
            imageUrl: String?
        ): BingoItemEditCommand =
            BingoItemEditCommand(id, bingoBoardId, memberId, title, subTitle, imageUrl)
    }
}

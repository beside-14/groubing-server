package com.beside.groubing.domain.bingo.event

class BingoLineCancelEvent(
    val bingoBoardId: Long,

    val memberId: Long,

    val otherMemberIds: List<Long>,

    val bingoBoardTitle: String,

    val bingoItemTitle: String
) {
    fun toMessage(nickname: String): String =
        "${nickname}님이 ${bingoBoardTitle} 빙고에서 달성한 빙고 중 ${bingoItemTitle} 빙고 아이템을 취소했어요."
}

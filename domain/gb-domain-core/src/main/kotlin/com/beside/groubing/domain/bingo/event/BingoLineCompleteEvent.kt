package com.beside.groubing.domain.bingo.event

class BingoLineCompleteEvent(
    val bingoBoardId: Long,

    val memberId: Long,

    val otherMemberIds: List<Long>,

    val bingoBoardTitle: String,

    val totalBingoCount: Int
) {
    fun toMessage(nickname: String): String =
        "${nickname}님이 ${bingoBoardTitle} 빙고를 ${totalBingoCount} 빙고 달성했어요!"
}

package com.beside.groubing.domain.bingo.event

class BingoCompleteEvent(
    val bingoBoardId: Long,

    val memberId: Long,

    val otherMemberIds: List<Long>,

    val bingoBoardTitle: String
) {
    fun toMessage(nickname: String): String =
        "${nickname}님이 ${bingoBoardTitle} 빙고의 목표 빙고 수를 달성했어요!"
}

package com.beside.groubing.domain.bingo.event

class BingoCompleteEvent(
    val bingoBoardId: Long,

    val memberId: Long,

    val otherMemberIds: List<Long>,

    val bingoBoardTitle: String
)

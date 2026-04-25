package com.beside.groubing.domain.bingo.event

class BingoLineCompleteEvent(
    val bingoBoardId: Long,

    val memberId: Long,

    val otherMemberIds: List<Long>,

    val bingoBoardTitle: String,

    val totalBingoCount: Int
)

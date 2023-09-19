package com.beside.groubing.groubingserver.domain.bingo.domain

class BingoItemCancelEvent(
    val bingoBoardId: Long,

    val memberId: Long,

    val otherMemberIds: List<Long>,

    val bingoBoardTitle: String,

    val bingoItemTitle: String
)

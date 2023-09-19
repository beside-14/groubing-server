package com.beside.groubing.groubingserver.domain.bingo.domain

class BingoCompleteEvent(
    val bingoBoardId: Long,

    val memberId: Long,

    val otherMemberIds: List<Long>,

    val bingoBoardTitle: String
)

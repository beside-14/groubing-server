package com.beside.groubing.groubingserver.domain.bingo.domain

class BingoItemCancelEvent(
    val bingoBoardId: Long,

    val memberId: Long,

    val bingoBoardTitle: String,

    val bingoItemTitle: String
)
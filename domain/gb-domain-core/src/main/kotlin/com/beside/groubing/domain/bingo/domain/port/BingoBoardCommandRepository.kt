package com.beside.groubing.domain.bingo.domain.port

import com.beside.groubing.domain.bingo.domain.BingoBoard

interface BingoBoardCommandRepository {
    fun save(bingoBoard: BingoBoard): BingoBoard

    fun update(bingoBoard: BingoBoard): BingoBoard

    fun updateMemo(bingoBoardId: Long, memo: String?)

    fun updateOpen(bingoBoardId: Long, open: Boolean)

    fun inactivateAllOf(memberId: Long)
}

package com.beside.groubing.groubingserver.domain.bingo.domain.port

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard

interface BingoBoardCommandRepository {
    fun save(bingoBoard: BingoBoard): BingoBoard

    fun update(bingoBoard: BingoBoard): BingoBoard

    fun inactivateAllOf(memberId: Long)
}

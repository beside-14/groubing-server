package com.beside.groubing.groubingserver.domain.bingo.domain.port

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard

interface BingoBoardQueryRepository {
    fun findOne(id: Long): BingoBoard

    fun findAllOf(memberId: Long): List<BingoBoard>
}

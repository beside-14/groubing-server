package com.beside.groubing.domain.bingo.domain.port

import com.beside.groubing.domain.bingo.domain.BingoBoard

interface BingoBoardQueryRepository {
    fun findOne(id: Long): BingoBoard

    fun findAllOf(memberId: Long): List<BingoBoard>

    fun findAllIdsOf(memberId: Long): List<Long>

    fun isLeaderOf(bingoBoardId: Long, memberId: Long): Boolean
}

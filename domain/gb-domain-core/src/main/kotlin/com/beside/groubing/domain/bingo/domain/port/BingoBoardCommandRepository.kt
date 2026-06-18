package com.beside.groubing.domain.bingo.domain.port

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.BingoGoal
import com.beside.groubing.domain.bingo.domain.BingoMember
import com.beside.groubing.domain.bingo.domain.BingoPeriod

interface BingoBoardCommandRepository {
    fun save(bingoBoard: BingoBoard): BingoBoard

    fun update(bingoBoard: BingoBoard): BingoBoard

    fun updateBase(bingoBoardId: Long, title: String, bingoGoal: BingoGoal, period: BingoPeriod)

    fun addBingoMembers(bingoBoardId: Long, bingoMembers: List<BingoMember>)

    fun updatePeriod(bingoBoardId: Long, period: BingoPeriod)

    fun updateMemo(bingoBoardId: Long, memo: String?)

    fun updateOpen(bingoBoardId: Long, open: Boolean)

    fun deactivateBingoMember(bingoBoardId: Long, memberId: Long)
}

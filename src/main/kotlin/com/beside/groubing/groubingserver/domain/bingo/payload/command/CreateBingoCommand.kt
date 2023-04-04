package com.beside.groubing.groubingserver.domain.bingo.payload.command

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoColor
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoType
import java.time.LocalDate

data class CreateBingoCommand(
    val memberId: Long,
    val title: String,
    val type: BingoType,
    val size: BingoSize,
    val color: BingoColor,
    val goal: Int,
    val open: Boolean,
    val since: LocalDate,
    val until: LocalDate
)

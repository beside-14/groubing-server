package com.beside.groubing.groubingserver.extension

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string

fun Arb.Companion.bingoBoard(
    memberId: Long = Arb.long().single(),
    bingoSize: Int = Arb.int(3..4).single(),
): Arb<BingoBoard> {
    return Arb.of(
        BingoBoard.createBingoBoard(
            memberId = memberId,
            title = Arb.string().single(),
            goal = Arb.int(1..bingoSize).single(),
            boardType = Arb.enum<BingoBoardType>().single(),
            open = Arb.boolean().single(),
            bingoSize = bingoSize
        )
    )
}

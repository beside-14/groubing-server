package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoLine
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction

class BingoLineResponse private constructor(
    val direction: Direction,

    val bingoItems: List<BingoItemResponse>
) {
    companion object {
        fun fromBingoLine(bingoLine: BingoLine, memberId: Long): BingoLineResponse {
            return BingoLineResponse(
                direction = bingoLine.direction,
                bingoItems = bingoLine.bingoItems
                    .map { BingoItemResponse.fromBingoItem(it, memberId) }
            )
        }
    }
}

package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction
import java.time.LocalDate

class BingoBoardOverviewResponse private constructor(
    val id: Long,

    val title: String,

    val since: LocalDate?,

    val until: LocalDate?,

    val goal: Int,

    val groupType: BingoBoardType,

    val open: Boolean,

    val bingoLines: List<BingoMapResponse.BingoLineResponse>,

    val totalCompleteCount: Int
) {
    companion object {
        fun fromBingoBoard(bingoBoard: BingoBoard, memberId: Long): BingoBoardOverviewResponse {
            val bingoMap = BingoMap(memberId, bingoBoard.size, bingoBoard.bingoItems)
            return BingoBoardOverviewResponse(
                id = bingoBoard.id,
                title = bingoBoard.title,
                since = bingoBoard.since,
                until = bingoBoard.until,
                goal = bingoBoard.goal,
                groupType = bingoBoard.boardType,
                open = bingoBoard.open,
                bingoLines = bingoMap.getBingoLines(Direction.HORIZONTAL)
                    .map { BingoMapResponse.BingoLineResponse.fromBingoLine(it, bingoMap.memberId) },
                totalCompleteCount = bingoMap.calculateTotalCompleteCount()
            )
        }
    }
}

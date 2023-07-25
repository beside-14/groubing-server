package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem
import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoLine
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

    val completed: Boolean,

    val finished: Boolean,

    val bingoLines: List<SimpleBingoLineResponse>,

    val totalBingoCount: Int
) {
    class SimpleBingoLineResponse private constructor(
        val direction: Direction,

        val bingoItems: List<SimpleBingoItemResponse>
    ) {
        companion object {
            fun fromBingoLine(bingoLine: BingoLine, memberId: Long): SimpleBingoLineResponse {
                return SimpleBingoLineResponse(
                    direction = bingoLine.direction,
                    bingoItems = bingoLine.bingoItems
                        .map { SimpleBingoItemResponse.fromBingoItem(it, memberId) }
                )
            }
        }
    }
    class SimpleBingoItemResponse private constructor(
        val itemOrder: Int,
        val complete: Boolean
    ) {
        companion object {
            fun fromBingoItem(bingoItem: BingoItem, memberId: Long): SimpleBingoItemResponse {
                return SimpleBingoItemResponse(
                    itemOrder = bingoItem.itemOrder,
                    complete = bingoItem.isCompleted(memberId)
                )
            }
        }
    }
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
                completed = bingoBoard.isCompleted(),
                finished = bingoBoard.isFinished(),
                bingoLines = bingoMap.getBingoLines(Direction.HORIZONTAL)
                    .map { SimpleBingoLineResponse.fromBingoLine(it, bingoMap.memberId) },
                totalBingoCount = bingoMap.calculateTotalBingoCount()
            )
        }
    }
}

package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem
import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoLine
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction

class BingoBoardResponse private constructor(
    val id: Long,

    val title: String,

    val goal: Int,

    val groupType: BingoBoardType,

    val open: Boolean,

    val dDay: String,

    val isStarted: Boolean,

    val isFinished: Boolean,

    val bingoSize: Int,

    val memo: String?,

    val bingoLines: List<BingoLineResponse>,

    val totalCompleteCount: Int,

    val horizontalCompleteLineIndexes: List<Int>,

    val verticalCompleteLineIndexes: List<Int>,

    val diagonalCompleteLineIndexes: List<Int>
) {

    class BingoItemResponse private constructor(
        val id: Long,
        val title: String?,
        val subTitle: String?,
        val imageUrl: String?,
        val complete: Boolean,
        val empty: Boolean
    ) {
        companion object {
            fun fromBingoItem(bingoItem: BingoItem, memberId: Long): BingoItemResponse {
                return BingoItemResponse(
                    id = bingoItem.id,
                    title = bingoItem.title,
                    subTitle = bingoItem.subTitle,
                    imageUrl = bingoItem.imageUrl,
                    empty = bingoItem.isEmpty(),
                    complete = bingoItem.isCompleted(memberId)
                )
            }
        }
    }

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

    companion object {
        fun fromBingoBoard(bingoBoard: BingoBoard, memberId: Long): BingoBoardResponse {
            val bingoMap = bingoBoard.makeBingoMap(memberId)
            return BingoBoardResponse(
                id = bingoBoard.id,
                title = bingoBoard.title,
                goal = bingoBoard.goal,
                groupType = bingoBoard.boardType,
                open = bingoBoard.open,
                dDay = "D-${bingoBoard.calculateLeftDays()}",
                memo = bingoBoard.memo,
                isStarted = bingoBoard.isStarted(),
                isFinished = bingoBoard.isFinished(),
                bingoSize = bingoBoard.size,
                bingoLines = bingoMap.getBingoLines(Direction.HORIZONTAL)
                    .map { BingoLineResponse.fromBingoLine(it, bingoMap.memberId) },
                totalCompleteCount = bingoMap.calculateTotalCompleteCount(),
                horizontalCompleteLineIndexes = bingoMap.getCompleteLineIndexes(Direction.HORIZONTAL),
                verticalCompleteLineIndexes = bingoMap.getCompleteLineIndexes(Direction.VERTICAL),
                diagonalCompleteLineIndexes = bingoMap.getCompleteLineIndexes(Direction.DIAGONAL)
            )
        }
    }
}

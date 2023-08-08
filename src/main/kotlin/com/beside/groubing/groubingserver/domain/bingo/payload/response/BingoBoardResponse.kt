package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction

class BingoBoardResponse private constructor(
    val id: Long,

    val title: String,

    val goal: Int,

    val groupType: BingoBoardType,

    val open: Boolean,

    val completed: Boolean,

    val finished: Boolean,

    val bingoSize: Int,

    val memo: String?,

    val bingoLines: List<BingoLineResponse>
) {
    companion object {
        fun fromBingoBoard(bingoBoard: BingoBoard, memberId: Long): BingoBoardResponse {
            val bingoMap = bingoBoard.makeBingoMap(memberId)
            return BingoBoardResponse(
                id = bingoBoard.id,
                title = bingoBoard.title,
                goal = bingoBoard.goal,
                groupType = bingoBoard.boardType,
                open = bingoBoard.open,
                memo = bingoBoard.memo,
                completed = bingoBoard.isCompleted(),
                finished = bingoBoard.isFinished(),
                bingoSize = bingoBoard.size,
                bingoLines = bingoMap.getBingoLines(Direction.HORIZONTAL)
                    .map { BingoLineResponse.fromBingoLine(it, bingoMap.memberId) }
            )
        }
    }
}

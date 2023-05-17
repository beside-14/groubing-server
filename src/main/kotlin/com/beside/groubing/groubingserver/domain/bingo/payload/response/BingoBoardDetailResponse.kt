package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.member.domain.Member

class BingoBoardDetailResponse private constructor(
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

    val bingoMap: BingoMapResponse,

    val otherBingoMaps: List<BingoMapResponse>
) {
    companion object {
        fun fromBingoBoard(bingoBoard: BingoBoard, member: Member, otherMembers: List<Member>): BingoBoardDetailResponse {
            return BingoBoardDetailResponse(
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
                bingoMap = BingoMapResponse.fromBingoMap(bingoBoard.makeBingoMap(member.id), member!!.nickname),
                otherBingoMaps = otherMembers
                    .map { BingoMapResponse.fromBingoMap(bingoBoard.makeBingoMap(it.id), it.nickname) }
            )
        }
    }
}


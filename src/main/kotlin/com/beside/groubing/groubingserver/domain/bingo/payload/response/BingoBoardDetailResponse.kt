package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.member.domain.Member
import java.time.LocalDate

class BingoBoardDetailResponse private constructor(
    val id: Long,

    val title: String,

    val goal: Int,

    val groupType: BingoBoardType,

    val open: Boolean,

    val dDay: Long,

    val since: LocalDate?,

    val until: LocalDate?,

    val completed: Boolean,

    val finished: Boolean,

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
                since = bingoBoard.since,
                until = bingoBoard.until,
                dDay = bingoBoard.calculateLeftDays(),
                memo = bingoBoard.memo,
                completed = bingoBoard.isCompleted(),
                finished = bingoBoard.isFinished(),
                bingoSize = bingoBoard.size,
                bingoMap = BingoMapResponse.fromBingoMap(bingoBoard.makeBingoMap(member.id), member.nickname),
                otherBingoMaps = otherMembers
                    .map { BingoMapResponse.fromBingoMap(bingoBoard.makeBingoMap(it.id), it.nickname) }
            )
        }
    }
}


package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoBoardDetail
import com.beside.groubing.domain.bingo.domain.BingoBoardType
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId
import java.time.LocalDate

class BingoBoardDetailResponse private constructor(
    @EncryptId(ObfuscationType.BINGO_BOARD)
    val id: Long,

    val title: String,

    val goal: Int,

    val groupType: BingoBoardType,

    val open: Boolean,

    val dDay: Long,

    val memo: String?,

    val isLeader: Boolean,

    val since: LocalDate?,

    val until: LocalDate?,

    val completed: Boolean,

    val finished: Boolean,

    val bingoSize: Int,

    val bingoMap: BingoMapResponse,

    val otherBingoMaps: List<BingoMapResponse>
) {
    companion object {
        fun of(detail: BingoBoardDetail): BingoBoardDetailResponse {
            val bingoBoard = detail.bingoBoard
            val viewer = detail.viewer
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
                isLeader = bingoBoard.isLeader(viewer.id),
                completed = bingoBoard.isStarted(),
                finished = bingoBoard.isFinished(),
                bingoSize = bingoBoard.size,
                bingoMap = BingoMapResponse.fromBingoMap(bingoBoard.makeBingoMap(viewer.id), viewer.nickname),
                otherBingoMaps = detail.otherMembers
                    .map { BingoMapResponse.fromBingoMap(bingoBoard.makeBingoMap(it.id), it.nickname) }
            )
        }
    }
}

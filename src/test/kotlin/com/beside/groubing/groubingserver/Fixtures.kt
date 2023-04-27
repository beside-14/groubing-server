package com.beside.groubing.groubingserver

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoGoal
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoMember
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoMemberType
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize

fun aEmptyBingo(): BingoBoard {
    val memberId = 1L
    return BingoBoard(
        id = 1L,
        bingoMembers = listOf(BingoMember.createBingoMember(memberId, BingoMemberType.LEADER)),
        title = "샘플 빙고",
        bingoGoal = BingoGoal.create(3, BingoSize.cache(3)),
        bingoSize = BingoSize.cache(3),
        boardType = BingoBoardType.GROUP,
        open = true,
        bingoItems = (1..9).map { BingoItem(id = it.toLong(), itemOrder = it) }
    )
}

fun aBingoBoard(): BingoBoard {
    val memberId = 1L
    val bingoBoard = aEmptyBingo()

    val bingoItem1 = BingoItem(id = 1L, itemOrder = 1, title = "영어 회화 만점.", subTitle = "2023년 1월달까지 영어 회화 만점.")
    val bingoItem2 = BingoItem(id = 2L, itemOrder = 2, title = "영어 토플 만점.", subTitle = "2023년 2월달까지 영어 토플 만점.")
    val bingoItem3 = BingoItem(id = 3L, itemOrder = 3, title = "영어 스피킹 만점.", subTitle = "2023년 3월달까지 영어 스피킹 만점.")
    val bingoItem4 = BingoItem(id = 4L, itemOrder = 4, title = "영어 토익 만점.", subTitle = "2023년 4월달까지 영어 스피킹 만점.")
    val bingoItem5 = BingoItem(id = 5L, itemOrder = 5, title = "영어 OPIC 만점.", subTitle = "2023년 5월달까지 영어 스피킹 만점.")
    val bingoItem7 = BingoItem(id = 7L, itemOrder = 7, title = "영어 토익스피킹 만점.", subTitle = "2023년 6월달까지 영어 스피킹 만점.")
    val bingoItem8 = BingoItem(id = 8L, itemOrder = 8, title = "영어 TAPS 만점.", subTitle = "2023년 7월달까지 영어 스피킹 만점.")

    bingoBoard.updateBingoItems(
        listOf(bingoItem1, bingoItem2, bingoItem3, bingoItem4, bingoItem5, bingoItem7, bingoItem8)
    )
    bingoBoard.completeBingoItem(1L, memberId)
    bingoBoard.completeBingoItem(2L, memberId)
    bingoBoard.completeBingoItem(3L, memberId)
    bingoBoard.completeBingoItem(4L, memberId)
    bingoBoard.completeBingoItem(5L, memberId)
    bingoBoard.completeBingoItem(7L, memberId)
    bingoBoard.completeBingoItem(8L, memberId)
    return bingoBoard
}

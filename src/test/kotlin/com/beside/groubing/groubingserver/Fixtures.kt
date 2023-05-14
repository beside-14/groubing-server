package com.beside.groubing.groubingserver

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoGoal
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoMember
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoMemberType
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoItemUpdateCommand
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern

fun aEmptyBingo(): BingoBoard {
    val memberId = 1L
    return BingoBoard(
        id = 1L,
        bingoMembers = mutableListOf(BingoMember.create(memberId, BingoMemberType.LEADER)),
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

    bingoBoard.updateBingoItem(
        memberId = memberId,
        bingoItemId = 1L,
        BingoItemUpdateCommand.createCommand(
            title = "영어 회화 만점.",
            subTitle = "2023년 1월달까지 영어 회화 만점."
        )
    )
    bingoBoard.updateBingoItem(
        memberId = memberId,
        bingoItemId = 2L,
        BingoItemUpdateCommand.createCommand(
            title = "영어 토플 만점.",
            subTitle = "2023년 2월달까지 영어 토플 만점."
        )
    )
    bingoBoard.updateBingoItem(
        memberId = memberId,
        bingoItemId = 3L,
        BingoItemUpdateCommand.createCommand(
            title = "영어 스피킹 만점.",
            subTitle = "2023년 3월달까지 영어 스피킹 만점."
        )
    )
    bingoBoard.updateBingoItem(
        memberId = memberId,
        bingoItemId = 4L,
        BingoItemUpdateCommand.createCommand(
            title = "영어 토익 만점.",
            subTitle = "2023년 4월달까지 영어 스피킹 만점."
        )
    )
    bingoBoard.updateBingoItem(
        memberId = memberId,
        bingoItemId = 5L,
        BingoItemUpdateCommand.createCommand(
            title = "영어 OPIC 만점.",
            subTitle = "2023년 5월달까지 영어 스피킹 만점."
        )
    )
    bingoBoard.updateBingoItem(
        memberId = memberId,
        bingoItemId = 6L,
        BingoItemUpdateCommand.createCommand(
            title = "영어 토익스피킹 만점.",
            subTitle = "2023년 6월달까지 영어 스피킹 만점."
        )
    )
    bingoBoard.updateBingoItem(
        memberId = memberId,
        bingoItemId = 7L,
        BingoItemUpdateCommand.createCommand(
            title = "영어 TAPS 만점.",
            subTitle = "2023년 7월달까지 영어 스피킹 만점."
        )
    )
    bingoBoard.updateBingoItem(
        memberId = memberId,
        bingoItemId = 8L,
        BingoItemUpdateCommand.createCommand(
            title = "영어 단어외우기 만점.",
            subTitle = "2023년 8월달까지 영어 단어외우기 만점."
        )
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

fun aMember(
    email: String = Arb.email(Arb.string(5, 10, Codepoint.alphanumeric()), Arb.stringPattern("groubing\\.com")).single(),
    password: String = Arb.string(minSize = 8, maxSize = 20, codepoints = Codepoint.alphanumeric()).single(),
    nickname: String = Arb.string(minSize = 8, maxSize = 20, codepoints = Codepoint.alphanumeric()).single()
): Member {
    return Member.create(
        email = email,
        password = password,
        nickname = nickname,
        role = MemberRole.MEMBER
    )
}

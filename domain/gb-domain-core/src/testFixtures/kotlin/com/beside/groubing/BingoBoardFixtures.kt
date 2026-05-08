package com.beside.groubing

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.BingoBoardType
import com.beside.groubing.domain.bingo.domain.BingoColor
import com.beside.groubing.domain.bingo.domain.BingoGoal
import com.beside.groubing.domain.bingo.domain.BingoItem
import com.beside.groubing.domain.bingo.domain.BingoItems
import com.beside.groubing.domain.bingo.domain.BingoMember
import com.beside.groubing.domain.bingo.domain.BingoMemberType
import com.beside.groubing.domain.bingo.domain.BingoMembers
import com.beside.groubing.domain.bingo.domain.BingoSize
import com.beside.groubing.domain.bingo.payload.command.BingoItemUpdateCommand
import java.time.LocalDate

fun aEmptyBingo(): BingoBoard {
    return aEmptyBingo(1L, BingoBoardType.GROUP, 1L, 1)
}

fun aEmptyBingo(bingoBoardId: Long, bingoBoardType: BingoBoardType, memberId: Long, startItemId: Int): BingoBoard {
    val bingoItemAlphabets = listOf(
        "g", "r", "o", "u", "b", "i", "n", "o2", "i2",
        "g", "r", "o", "u", "b", "i", "n"
    )
    val numberRange = bingoItemAlphabets.shuffled().toMutableList()
    val size = BingoSize.cache(3)
    return BingoBoard.of(
        id = bingoBoardId,
        title = "샘플 빙고${bingoBoardId}",
        boardType = bingoBoardType,
        bingoColor = BingoColor.makeRandomBingoColor(),
        open = true,
        memo = null,
        active = true,
        bingoSize = size,
        bingoGoal = BingoGoal.create(3, size),
        period = null,
        bingoMembers = BingoMembers.of(mutableListOf(BingoMember.create(memberId, BingoMemberType.LEADER))),
        bingoItems = BingoItems.of(
            (1 + ((startItemId - 1) * 9)..(9 * startItemId)).map {
                BingoItem.of(
                    id = it.toLong(),
                    title = null,
                    subTitle = null,
                    imageUrl = numberRange.removeAt(0),
                    itemOrder = it % 9,
                    colorCode = "#2787C9",
                    completeMembers = mutableSetOf()
                )
            }
        )
    )
}

fun aTemporaryBingo(): BingoBoard {
    val memberId = 1L
    val bingoBoard = aEmptyBingo()
    bingoBoard.bingoItems.forEachIndexed { index, bingoItem ->
        val command = BingoItemUpdateCommand.of("코딩공부 ${index + 1}", "2023년 ${index + 1}월달까지 코딩공부")
        bingoBoard.updateBingoItem(
            memberId = memberId,
            bingoItemId = bingoItem.id,
            title = command.title,
            subTitle = command.subTitle
        )
    }
    return bingoBoard
}

fun createBingoBoard(
    bingoBoardId: Long,
    bingoBoardType: BingoBoardType,
    memberId: Long,
    startItemId: Int,
    titlePrefix: String,
    bingoMembers: List<Long>
): BingoBoard {
    val bingoBoard = aEmptyBingo(bingoBoardId, bingoBoardType, memberId, startItemId)
    bingoBoard.bingoItems.forEachIndexed { index, bingoItem ->
        val command =
            BingoItemUpdateCommand.of("$titlePrefix ${index + 1}", "2023년 ${index + 1}월달까지 $titlePrefix")
        bingoBoard.updateBingoItem(
            memberId = memberId,
            bingoItemId = bingoItem.id,
            title = command.title,
            subTitle = command.subTitle
        )
    }

    bingoBoard.updateBingoMembersPeriod(
        memberId = memberId,
        bingoMembers = bingoMembers,
        since = LocalDate.now(),
        until = LocalDate.now().plusDays(7)
    )

    return bingoBoard
}

fun aEnglishStudyBingoBoard(): BingoBoard {
    return createBingoBoard(2L, BingoBoardType.GROUP, 1L, 1, "영어공부", listOf(2, 3, 7))
}

fun aHealthBingoBoard(): BingoBoard {
    return createBingoBoard(3L, BingoBoardType.GROUP, 1L, 2, "운동하기", listOf(3, 6, 9))
}

fun aGameBingoBoard(): BingoBoard {
    return createBingoBoard(4L, BingoBoardType.GROUP, 1L, 3, "게임하기", listOf(2, 4, 10))
}

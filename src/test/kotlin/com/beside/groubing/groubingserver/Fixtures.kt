package com.beside.groubing.groubingserver

import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.ENUM
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoGoal
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoMember
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoMemberType
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoItemUpdateCommand
import com.beside.groubing.groubingserver.domain.member.domain.Member
import java.time.LocalDate

fun aEmptyBingo(): BingoBoard {
    return aEmptyBingo(1L, BingoBoardType.GROUP, 1L, 1)
}

fun aEmptyBingo(bingoBoardId: Long, bingoBoardType: BingoBoardType, memberId: Long, startItemId: Int): BingoBoard {
    return BingoBoard(
        id = bingoBoardId,
        bingoMembers = mutableListOf(BingoMember.create(memberId, BingoMemberType.LEADER)),
        title = "샘플 빙고${bingoBoardId}",
        bingoGoal = BingoGoal.create(3, BingoSize.cache(3)),
        bingoSize = BingoSize.cache(3),
        boardType = bingoBoardType,
        open = true,
        bingoItems = (1 + ((startItemId - 1) * 9)..(9 * startItemId)).map { BingoItem(id = it.toLong(), itemOrder = it) }
    )
}

fun aEnglishStudyBingoBoard(): BingoBoard {
    val memberId = 1L
    val bingoBoard = aEmptyBingo()

    bingoBoard.bingoItems.forEachIndexed { index, bingoItem ->
        bingoBoard.updateBingoItem(memberId = memberId, bingoItemId = bingoItem.id,
            BingoItemUpdateCommand.createCommand("영어공부 ${index + 1}", "2023년 ${index + 1}월달까지 영어공부"))
    }

    bingoBoard.updateBingoMembersPeriod(
        memberId = memberId,
        bingoMembers = listOf(2, 3, 7),
        since = LocalDate.now(),
        until = LocalDate.now().plusDays(7)
    )

    (1L..5L).forEach { bingoBoard.completeBingoItem(it, memberId) }
    bingoBoard.completeBingoItem(7L, memberId)
    bingoBoard.completeBingoItem(8L, memberId)

    return bingoBoard
}

fun aHealthBingoBoard(): BingoBoard {
    val memberId = 1L
    val bingoBoard = aEmptyBingo(bingoBoardId = 2L, BingoBoardType.SINGLE, memberId, 2)

    bingoBoard.bingoItems.forEachIndexed { index, bingoItem ->
        bingoBoard.updateBingoItem(memberId = memberId, bingoItemId = bingoItem.id,
            BingoItemUpdateCommand.createCommand("운동하기 ${index + 1}", "2023년 ${index + 1}월달까지 운동하기"))
    }

    bingoBoard.updateBingoMembersPeriod(
        memberId = memberId,
        bingoMembers = listOf(3, 6, 9),
        since = LocalDate.now(),
        until = LocalDate.now().plusDays(7)
    )

    (1L..7L).forEach { bingoBoard.completeBingoItem(it, memberId) }

    return bingoBoard
}

fun aGameBingoBoard(): BingoBoard {
    val memberId = 1L
    val bingoBoard = aEmptyBingo(bingoBoardId = 3L, BingoBoardType.SINGLE, memberId, 3)

    bingoBoard.bingoItems.forEachIndexed { index, bingoItem ->
        bingoBoard.updateBingoItem(memberId = memberId, bingoItemId = bingoItem.id,
            BingoItemUpdateCommand.createCommand("게임하기 ${index + 1}", "2023년 ${index + 1}월달까지 게임하기"))
    }

    bingoBoard.updateBingoMembersPeriod(
        memberId = memberId,
        bingoMembers = listOf(2, 4, 10),
        since = LocalDate.now(),
        until = LocalDate.now().plusDays(7)
    )

    (1L..5L).forEach { bingoBoard.completeBingoItem(it, memberId) }
    bingoBoard.completeBingoItem(7L, memberId)

    return bingoBoard
}

fun aMember(memberId: Long): Member {
    return Member(id = memberId, email = "test${memberId}@gmail.com", "1234", "test${memberId}")
}

val bingoBoardResponseSnippets = responseBody(
    "id" responseType NUMBER means "빙고 ID" example "1",
    "title" responseType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}",
    "goal" responseType NUMBER means "달성 목표수, 빙고 사이즈가 3X3 인 경우 최대 3개, 4X4 인 경우 최대 4개" example "1",
    "groupType" responseType ENUM(BingoBoardType::class) means "빙고 유형" example "`SINGLE`" formattedAs "개인 : `SINGLE`, 그룹 : `GROUP`",
    "open" responseType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false",
    "dday" responseType STRING means "빙고 종료일자까지 남은 일 카운트",
    "isStarted" responseType BOOLEAN means "빙고 보드의 임시저장 여부, 시작/종료일이 설정되지 않은 경우 임시저장으로 간주합니다." example "false" formattedAs "`true` : 임시저장 상태, `false` : 발행 상태",
    "isFinished" responseType BOOLEAN means "빙고 보드의 진행 종료 여부, D-Day 기준으로 종료 여부를 확인합니다." example "false" formattedAs "`true` : 빙고 종료, `false` : 빙고 진행 중",
    "bingoSize" responseType NUMBER means "빙고 사이즈" example "3" formattedAs "3X3 : 3, 4X4 : 4",
    "memo" responseType STRING means "빙고 메모" example "빙고 메모이며 `null` 일 수 있습니다.",
    "bingoLines[].direction" responseType ENUM(Direction::class) means "빙고 축을 의미합니다." example "`HORIZONTAL`" formattedAs "X : `HORIZONTAL`, Y : `VERTICAL`, Z : `DIAGONAL`",
    "bingoLines[].bingoItems[].id" responseType NUMBER means "빙고 아이템 ID" example "1",
    "bingoLines[].bingoItems[].title" responseType STRING means "TODO" example "토익 만점 받기",
    "bingoLines[].bingoItems[].subTitle" responseType STRING means "TODO 부가 설명, `null` 일 수 있습니다." example "토익 만점을 받으려면 열심히 공부해야 한다.",
    "bingoLines[].bingoItems[].imageUrl" responseType STRING means "빙고 아이템 추가 이미지 URL, `null` 일 수 있습니다.",
    "bingoLines[].bingoItems[].complete" responseType BOOLEAN means "TODO 달성 여부" example "true",
    "bingoLines[].bingoItems[].itemOrder" responseType NUMBER means "빙고 아이템 순서" example "1, 2, 3..."
)

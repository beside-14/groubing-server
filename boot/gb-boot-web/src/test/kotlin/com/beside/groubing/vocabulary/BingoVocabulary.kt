package com.beside.groubing.vocabulary

import com.beside.groubing.docs.ARRAY
import com.beside.groubing.docs.BOOLEAN
import com.beside.groubing.docs.DATE
import com.beside.groubing.docs.ENUM
import com.beside.groubing.docs.NUMBER
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.requestParam
import com.beside.groubing.docs.responseType
import com.beside.groubing.domain.bingo.domain.BingoBoardType
import com.beside.groubing.domain.bingo.domain.map.Direction

// --- Path / Query ---

fun bingoBoardIdPath(fieldName: String = "bingoBoardId") =
    fieldName requestParam "빙고 ID (obfuscated)"

// --- BingoBoard 응답 ---

fun bingoBoardId(fieldName: String = "id") =
    fieldName responseType STRING means "빙고 ID (obfuscated)" example "MEbN4aLpqRzK"

fun bingoTitle(fieldName: String = "title") =
    fieldName responseType STRING means
        "빙고 제목" example
        "[테스트] 새로운 빙고입니다." formattedAs
        "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}"

fun bingoGoal(fieldName: String = "goal") =
    fieldName responseType NUMBER means "달성 목표수, 빙고 사이즈가 3X3 인 경우 최대 3개, 4X4 인 경우 최대 4개" example "1"

fun bingoBoardType(fieldName: String = "groupType") =
    fieldName responseType ENUM(BingoBoardType::class) means
        "빙고 유형" example
        "`SINGLE`" formattedAs
        "개인 : `SINGLE`, 그룹 : `GROUP`"

fun bingoOpen(fieldName: String = "open") =
    fieldName responseType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false"

fun bingoDday(fieldName: String = "dday") =
    fieldName responseType STRING means "빙고 종료일자까지 남은 일 카운트"

fun bingoCompleted(fieldName: String = "completed") =
    fieldName responseType BOOLEAN means
        "빙고 보드의 임시저장 여부, 시작/종료일이 설정되지 않은 경우 임시저장으로 간주합니다." example
        "false" formattedAs
        "`true` : 임시저장 상태, `false` : 발행 상태"

fun bingoFinished(fieldName: String = "finished") =
    fieldName responseType BOOLEAN means
        "빙고 보드의 진행 종료 여부, D-Day 기준으로 종료 여부를 확인합니다." example
        "false" formattedAs
        "`true` : 빙고 종료, `false` : 빙고 진행 중"

fun bingoSize(fieldName: String = "bingoSize") =
    fieldName responseType NUMBER means "빙고 사이즈" example "3" formattedAs "3X3 : 3, 4X4 : 4"

fun bingoMemo(fieldName: String = "memo") =
    fieldName responseType STRING means "빙고 메모" example "빙고 메모이며 `null` 일 수 있습니다."

fun bingoIsLeader(fieldName: String = "isLeader") =
    fieldName responseType BOOLEAN means "해당 빙고 리더 여부 `true` : 리더,`false` : 멤버" example "true"

fun bingoSince(fieldName: String = "since") =
    fieldName responseType DATE means "빙고 시작일자, 현재보다 미래로 설정" example "2023-01-01" formattedAs "yyyy-MM-dd"

fun bingoUntil(fieldName: String = "until") =
    fieldName responseType DATE means "빙고 종료일자, 시작일자보다 미래로 설정" example "2023-02-01" formattedAs "yyyy-MM-dd"

fun bingoColorValue(fieldName: String = "bingoColorValue") =
    fieldName responseType STRING means
        "빙고 Color" example
        "#B8B7FC" formattedAs
        "Blue : `#8BC0FC`, Orange: `#FCB179`, Red: `FF8282`, Green: `#55DEB5`, Purple : `#B8B7FC`"

fun bingoMembers(fieldName: String = "bingoMembers") =
    fieldName responseType ARRAY means "빙고 참여 멤버 ID 리스트" example "[2, 3, 7]"

// --- Bingo Lines / Items ---

fun bingoLineDirection(fieldName: String) =
    fieldName responseType ENUM(Direction::class) means
        "빙고 축을 의미합니다." example
        "`HORIZONTAL`" formattedAs
        "X : `HORIZONTAL`, Y : `VERTICAL`, Z : `DIAGONAL`"

fun bingoItemId(fieldName: String) =
    fieldName responseType STRING means "빙고 아이템 ID (obfuscated)" example "MEbN4aLpqRzK"

fun bingoItemTitle(fieldName: String) =
    fieldName responseType STRING means "TODO" example "토익 만점 받기"

fun bingoItemSubTitle(fieldName: String) =
    fieldName responseType STRING means "TODO 부가 설명, `null` 일 수 있습니다." example "토익 만점을 받으려면 열심히 공부해야 한다."

fun bingoItemImageUrl(fieldName: String) =
    fieldName responseType STRING means "빙고 아이템 추가 이미지 URL, `null` 일 수 있습니다."

fun bingoItemComplete(fieldName: String) =
    fieldName responseType BOOLEAN means "TODO 달성 여부" example "true"

fun bingoItemOrder(fieldName: String) =
    fieldName responseType NUMBER means "빙고 아이템 순서" example "1, 2, 3..."

fun bingoItemColorCode(fieldName: String) =
    fieldName responseType STRING means "빙고 아이템 Color Code" example "#F6A973"

// --- BingoMap ---

fun bingoMapNickName(fieldName: String, description: String = "빙고 참여자 닉네임") =
    fieldName responseType STRING means description example "holeman79"

fun totalBingoCount(fieldName: String) =
    fieldName responseType NUMBER means "달성한 총 빙고 수"

fun horizontalBingoIndexes(fieldName: String) =
    fieldName responseType ARRAY means "X축 달성한 빙고 아이템 인덱스"

fun verticalBingoIndexes(fieldName: String) =
    fieldName responseType ARRAY means "Y축 달성한 빙고 아이템 인덱스"

fun diagonalBingoIndexes(fieldName: String) =
    fieldName responseType ARRAY means "Z축 달성한 빙고 아이템 인덱스"

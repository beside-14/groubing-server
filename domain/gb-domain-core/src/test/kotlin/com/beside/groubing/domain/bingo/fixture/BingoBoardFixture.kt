package com.beside.groubing.domain.bingo.fixture

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.BingoBoardType
import com.beside.groubing.domain.bingo.domain.BingoColor
import com.beside.groubing.domain.bingo.domain.BingoGoal
import com.beside.groubing.domain.bingo.domain.BingoItem
import com.beside.groubing.domain.bingo.domain.BingoItems
import com.beside.groubing.domain.bingo.domain.BingoMember
import com.beside.groubing.domain.bingo.domain.BingoMemberType
import com.beside.groubing.domain.bingo.domain.BingoMembers
import com.beside.groubing.domain.bingo.domain.BingoPeriod
import com.beside.groubing.domain.bingo.domain.BingoSize
import java.time.LocalDate

/**
 * 테스트에서 BingoBoard 애그리거트를 결정적(deterministic)으로 조립하기 위한 Fixture.
 *
 * 프로덕션 [BingoBoard.create] 는 아이템 order/이미지/색상을 랜덤으로 섞기 때문에
 * 빙고 라인 완성 같은 위치 의존 테스트에는 부적합하다.
 * 이 Fixture 는 [BingoBoard.of] 와 [BingoItem.of] 를 사용해 itemOrder 가 1..N*N 으로
 * 고정되고, 모든 아이템에 title 이 채워진(=isAllUpdated) 보드를 만든다.
 */
object BingoBoardFixture {

    const val LEADER_ID = 1L
    const val PARTICIPANT_ID = 2L

    /**
     * itemOrder 가 1..(size*size) 로 채워진, 모든 아이템에 title 이 있는 BingoItems.
     * 인덱스 i 의 아이템 id 는 (i+1), itemOrder 도 (i+1) 로 1:1 매핑된다.
     *
     * @param updated false 면 일부(첫 아이템)의 title 을 null 로 두어 isAllUpdated=false 를 만든다.
     */
    fun bingoItems(size: Int = 3, updated: Boolean = true): BingoItems {
        val total = size * size
        val items = (1..total).map { order ->
            BingoItem.of(
                id = order.toLong(),
                title = if (updated) "item-$order" else if (order == 1) null else "item-$order",
                subTitle = null,
                imageUrl = "g",
                itemOrder = order,
                colorCode = "#2787C9",
                completeMembers = mutableSetOf()
            )
        }
        return BingoItems.of(items)
    }

    fun bingoMembers(
        leaderId: Long = LEADER_ID,
        participantIds: List<Long> = emptyList()
    ): BingoMembers {
        val members = mutableListOf(
            BingoMember.of(id = 1L, memberId = leaderId, bingoMemberType = BingoMemberType.LEADER, active = true)
        )
        participantIds.forEachIndexed { index, memberId ->
            members.add(
                BingoMember.of(
                    id = (index + 2).toLong(),
                    memberId = memberId,
                    bingoMemberType = BingoMemberType.PARTICIPANT,
                    active = true
                )
            )
        }
        return BingoMembers.of(members)
    }

    /**
     * 시작된(임시빙고가 아닌) 그룹 빙고 보드. period 가 세팅되어 있고 모든 아이템에 title 이 있어 isStarted=true.
     */
    fun startedBoard(
        id: Long = 1L,
        size: Int = 3,
        goal: Int = 1,
        leaderId: Long = LEADER_ID,
        participantIds: List<Long> = emptyList(),
        until: LocalDate = LocalDate.now().plusDays(7),
        open: Boolean = true
    ): BingoBoard {
        val bingoSize = BingoSize.cache(size)
        return BingoBoard.of(
            id = id,
            title = "그루빙 빙고",
            boardType = BingoBoardType.GROUP,
            bingoColor = BingoColor.BLUE,
            open = open,
            memo = null,
            active = true,
            bingoSize = bingoSize,
            bingoGoal = BingoGoal.create(goal, bingoSize),
            period = BingoPeriod.of(LocalDate.now(), until),
            bingoMembers = bingoMembers(leaderId, participantIds),
            bingoItems = bingoItems(size, updated = true)
        )
    }

    /**
     * 임시(temporary) 빙고 보드. period 가 null 이라 isStarted=false → shuffle 가능, complete/cancel 불가.
     */
    fun temporaryBoard(
        id: Long = 1L,
        size: Int = 3,
        goal: Int = 1,
        leaderId: Long = LEADER_ID,
        participantIds: List<Long> = emptyList(),
        allUpdated: Boolean = false
    ): BingoBoard {
        val bingoSize = BingoSize.cache(size)
        return BingoBoard.of(
            id = id,
            title = "임시 빙고",
            boardType = BingoBoardType.GROUP,
            bingoColor = BingoColor.BLUE,
            open = true,
            memo = null,
            active = true,
            bingoSize = bingoSize,
            bingoGoal = BingoGoal.create(goal, bingoSize),
            period = null,
            bingoMembers = bingoMembers(leaderId, participantIds),
            bingoItems = bingoItems(size, updated = allUpdated)
        )
    }
}

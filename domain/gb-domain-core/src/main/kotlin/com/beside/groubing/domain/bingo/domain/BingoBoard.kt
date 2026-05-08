package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.domain.map.BingoMap
import com.beside.groubing.domain.bingo.event.BingoCompleteEvent
import com.beside.groubing.domain.bingo.event.BingoLineCancelEvent
import com.beside.groubing.domain.bingo.event.BingoLineCompleteEvent
import com.beside.groubing.domain.bingo.exception.BingoIllegalStateException
import com.beside.groubing.domain.bingo.exception.BingoInputException
import java.time.LocalDate

class BingoBoard private constructor(
    val id: Long,
    title: String,
    val boardType: BingoBoardType,
    val bingoColor: BingoColor,
    open: Boolean,
    memo: String?,
    active: Boolean,
    val bingoSize: BingoSize,
    bingoGoal: BingoGoal,
    period: BingoPeriod?,
    val bingoMembers: BingoMembers,
    val bingoItems: BingoItems
) {
    var title: String = title
        private set

    var open: Boolean = open
        private set

    var memo: String? = memo
        private set

    var active: Boolean = active
        private set

    var bingoGoal: BingoGoal = bingoGoal
        private set

    var period: BingoPeriod? = period
        private set

    private val domainEvents: MutableList<Any> = mutableListOf()

    fun pullEvents(): List<Any> {
        val events = domainEvents.toList()
        domainEvents.clear()
        return events
    }

    private fun registerEvent(event: Any) {
        domainEvents.add(event)
    }

    val size: Int
        get() = bingoSize.size

    val goal: Int
        get() = bingoGoal.goal

    val since: LocalDate?
        get() = period?.since

    val until: LocalDate?
        get() = period?.until

    fun calculateLeftDays(): Long = maxOf(0L, period?.calculateLeftDays() ?: 0L)

    fun makeBingoMap(memberId: Long): BingoMap = BingoMap(memberId, size, bingoItems.sortedByOrder())

    fun isStarted(): Boolean = period != null && bingoItems.isAllUpdated()

    fun isFinished(): Boolean = period?.isExpired() ?: false

    fun isLeader(memberId: Long): Boolean = bingoMembers.isLeaderOf(memberId)

    fun validateAuthority(memberId: Long) = bingoMembers.validateLeaderOf(memberId, id)

    fun updateBingoItem(memberId: Long, bingoItemId: Long, title: String, subTitle: String?): BingoItem {
        bingoMembers.validateLeaderOf(memberId, id)
        val bingoItem = bingoItems.findOf(bingoItemId)
        bingoItem.update(title = title, subTitle = subTitle)
        return bingoItem
    }

    fun updateBase(memberId: Long, title: String, goal: Int, since: LocalDate, until: LocalDate) {
        bingoMembers.validateLeaderOf(memberId, id)
        this.title = title
        this.bingoGoal = BingoGoal.create(goal, bingoSize)
        this.period = BingoPeriod.create(since, until)
    }

    fun updateBingoMembersPeriod(memberId: Long, bingoMembers: List<Long>, since: LocalDate, until: LocalDate) {
        this.bingoMembers.validateLeaderOf(memberId, id)
        this.period = BingoPeriod.create(since, until)
        this.bingoMembers.addNewMembers(bingoMembers)
    }

    fun updateBingoMemo(memberId: Long, memo: String?) {
        bingoMembers.validateLeaderOf(memberId, id)
        this.memo = memo
    }

    fun updateBingoOpen(memberId: Long, open: Boolean) {
        bingoMembers.validateLeaderOf(memberId, id)
        this.open = open
    }

    fun completeBingoItem(bingoItemId: Long, memberId: Long) {
        if (!isStarted()) {
            throw BingoIllegalStateException("아직 임시빙고 상태에서 빙고를 완성할 수 없습니다.")
        }
        val bingoMap = makeBingoMap(memberId)
        val beforeBingoCount = bingoMap.calculateTotalBingoCount()
        bingoItems.findOf(bingoItemId).complete(memberId)
        val afterBingoCount = bingoMap.calculateTotalBingoCount()
        registerBingoItemCompleteEvent(afterBingoCount, beforeBingoCount, memberId)
    }

    private fun registerBingoItemCompleteEvent(afterBingoCount: Int, beforeBingoCount: Int, memberId: Long) {
        if (afterBingoCount > beforeBingoCount) {
            registerEvent(
                BingoLineCompleteEvent(
                    bingoBoardId = id,
                    bingoBoardTitle = title,
                    totalBingoCount = afterBingoCount,
                    memberId = memberId,
                    otherMemberIds = bingoMembers.otherMemberIdsOf(memberId)
                )
            )
            return
        }
        if (bingoGoal.isGoal(afterBingoCount)) {
            registerEvent(
                BingoCompleteEvent(
                    bingoBoardId = id,
                    bingoBoardTitle = title,
                    memberId = memberId,
                    otherMemberIds = bingoMembers.otherMemberIdsOf(memberId)
                )
            )
        }
    }

    fun cancelBingoItem(bingoItemId: Long, memberId: Long) {
        if (!isStarted()) {
            throw BingoIllegalStateException("아직 임시빙고 상태에서 빙고를 취소할 수 없습니다.")
        }
        val bingoMap = makeBingoMap(memberId)
        val beforeBingoCount = bingoMap.calculateTotalBingoCount()
        val bingoItem = bingoItems.findOf(bingoItemId)
        bingoItem.cancel(memberId)
        val afterBingoCount = bingoMap.calculateTotalBingoCount()
        if (afterBingoCount < beforeBingoCount) {
            registerEvent(
                BingoLineCancelEvent(
                    bingoBoardId = id,
                    bingoBoardTitle = title,
                    bingoItemTitle = bingoItem.title!!,
                    memberId = memberId,
                    otherMemberIds = bingoMembers.otherMemberIdsOf(memberId)
                )
            )
        }
    }

    fun shuffleBingoItems() {
        if (isStarted()) {
            throw BingoInputException("임시 빙고가 아니면 shuffle 할 수 없습니다. BingoBoard Id : $id")
        }
        bingoItems.shuffle()
        bingoItems.initColors(size)
    }

    fun delete() {
        this.active = false
    }

    fun validateViewableBy(memberId: Long) {
        if (!isLeader(memberId) && !isStarted() && bingoMembers.findOf(memberId, id).active) {
            throw BingoInputException("접근할 수 없는 빙고보드입니다.")
        }
    }

    fun validateCanLeave(memberId: Long) {
        if (isLeader(memberId)) {
            throw BingoIllegalStateException("그룹 빙고 리더는 빙고를 나갈 수 없습니다.")
        }
    }

    fun inactiveByMemberId(memberId: Long) {
        bingoMembers.inactivateOf(memberId, id)
        bingoItems.completeMembersOf(memberId).forEach { it?.inactive() }
    }

    fun otherActiveMemberIdsOf(memberId: Long): List<Long> = bingoMembers.otherActiveMemberIdsOf(memberId)

    companion object {
        private val BINGO_ITEM_ALPHABETS = listOf(
            "g", "r", "o", "u", "b", "i", "n",
            "b2", "b3", "g2", "i2", "i3", "n2", "o2", "r2", "u2"
        )

        fun create(
            memberId: Long,
            title: String,
            goal: Int,
            boardType: BingoBoardType,
            open: Boolean,
            bingoSize: Int
        ): BingoBoard {
            val size = BingoSize.cache(bingoSize)
            return BingoBoard(
                id = 0L,
                title = title,
                boardType = boardType,
                bingoColor = BingoColor.makeRandomBingoColor(),
                open = open,
                memo = null,
                active = true,
                bingoSize = size,
                bingoGoal = BingoGoal.create(goal, size),
                period = null,
                bingoMembers = BingoMembers.ofLeader(memberId),
                bingoItems = BingoItems.create(bingoSize, BINGO_ITEM_ALPHABETS)
            )
        }

        fun of(
            id: Long,
            title: String,
            boardType: BingoBoardType,
            bingoColor: BingoColor,
            open: Boolean,
            memo: String?,
            active: Boolean,
            bingoSize: BingoSize,
            bingoGoal: BingoGoal,
            period: BingoPeriod?,
            bingoMembers: BingoMembers,
            bingoItems: BingoItems
        ): BingoBoard {
            return BingoBoard(
                id = id,
                title = title,
                boardType = boardType,
                bingoColor = bingoColor,
                open = open,
                memo = memo,
                active = active,
                bingoSize = bingoSize,
                bingoGoal = bingoGoal,
                period = period,
                bingoMembers = bingoMembers,
                bingoItems = bingoItems
            )
        }
    }
}

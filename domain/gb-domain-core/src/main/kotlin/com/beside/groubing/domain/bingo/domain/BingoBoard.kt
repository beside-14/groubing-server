package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.domain.map.BingoMap
import com.beside.groubing.domain.bingo.event.BingoCompleteEvent
import com.beside.groubing.domain.bingo.event.BingoLineCancelEvent
import com.beside.groubing.domain.bingo.event.BingoLineCompleteEvent
import com.beside.groubing.domain.bingo.exception.BingoIllegalStateException
import com.beside.groubing.domain.bingo.exception.BingoInputException
import java.time.LocalDate
import kotlin.random.Random

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
    val bingoMembers: MutableList<BingoMember>,
    val bingoItems: List<BingoItem>
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

    fun initBingoItemColor() {
        val bingoItemColors = mutableListOf(
            "#2787C9", "#F18FA2", "#E75097", "#FFD643", "#B6B4DB", "#00AAB3", "#00A783", "#85BCE7", "#F6A973"
        ).shuffled()

        bingoItems.sortedBy { it.itemOrder }
            .take(bingoItemColors.size)
            .forEachIndexed { index, bingoItem ->
                bingoItem.initItemColorCode(bingoItemColors[index])
            }

        bingoItems.sortedBy { it.itemOrder }
            .drop(bingoItemColors.size)
            .forEach { bingoItem ->
                val possibleColors = bingoItemColors.filter { color ->
                    !getNeighbors(bingoItem.itemOrder - 1).contains(color)
                }
                bingoItem.initItemColorCode(possibleColors.random())
            }
    }

    private fun getNeighbors(pos: Int): List<String?> {
        val neighbors = mutableListOf<String?>()
        val row = pos / size
        val col = pos % size

        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue
                val newRow = row + i
                val newCol = col + j
                val newIndex = newRow * size + newCol
                if (newRow in 0 until size && newCol in 0 until size) {
                    val neighborBingoItem = bingoItems.first { it.itemOrder == newIndex + 1 }
                    neighbors.add(neighborBingoItem.colorCode)
                }
            }
        }
        return neighbors
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

    fun makeBingoMap(memberId: Long): BingoMap {
        return BingoMap(memberId, size, bingoItems.sortedBy { it.itemOrder })
    }

    fun getOtherBingoMemberIds(memberId: Long): List<Long> {
        return bingoMembers.filter { it.memberId != memberId && it.active }
            .map { it.memberId }
    }

    fun isStarted(): Boolean {
        return period != null && bingoItems.all { it.isUpdated() }
    }

    fun isFinished(): Boolean = period?.isExpired() ?: false

    fun updateBingoItem(memberId: Long, bingoItemId: Long, title: String, subTitle: String?): BingoItem {
        validateAuthority(memberId)
        val bingoItem = findBingoItem(bingoItemId)
        bingoItem.update(title = title, subTitle = subTitle)
        return bingoItem
    }

    private fun findBingoItem(bingoItemId: Long): BingoItem {
        return bingoItems.find { it.id == bingoItemId }
            ?: throw BingoIllegalStateException("입력된 빙고 아이템 아이디가 잘못 되었습니다. id : $bingoItemId")
    }

    fun updateBase(memberId: Long, title: String, goal: Int, since: LocalDate, until: LocalDate) {
        validateAuthority(memberId)
        this.title = title
        this.bingoGoal = BingoGoal.create(goal, bingoSize)
        this.period = BingoPeriod.create(since, until)
    }

    fun updateBingoMembersPeriod(memberId: Long, bingoMembers: List<Long>, since: LocalDate, until: LocalDate) {
        validateAuthority(memberId)
        this.period = BingoPeriod.create(since, until)
        this.bingoMembers.addAll(bingoMembers.map { BingoMember.create(it) })
    }

    fun updateBingoMemo(memberId: Long, memo: String?) {
        validateAuthority(memberId)
        this.memo = memo
    }

    fun updateBingoOpen(memberId: Long, open: Boolean) {
        validateAuthority(memberId)
        this.open = open
    }

    fun validateAuthority(memberId: Long) {
        val bingoMember = bingoMembers.find { it.memberId == memberId }
            ?: throw BingoInputException("해당 그룹 빙고에 포함되지 않은 빙고 memberId입니다. bingoBoardId: $id, memberId: $memberId")
        if (!bingoMember.isLeader()) {
            throw BingoIllegalStateException("해당 빙고를 수정할 권한이 없습니다.")
        }
    }

    fun completeBingoItem(bingoItemId: Long, memberId: Long) {
        if (!isStarted()) {
            throw BingoIllegalStateException("아직 임시빙고 상태에서 빙고를 완성할 수 없습니다.")
        }
        val bingoMap = makeBingoMap(memberId)
        val beforeBingoCount = bingoMap.calculateTotalBingoCount()
        findBingoItem(bingoItemId).complete(memberId)
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
                    otherMemberIds = bingoMembers.filter { it.memberId != memberId }.map { it.memberId }
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
                    otherMemberIds = bingoMembers.filter { it.memberId != memberId }.map { it.memberId }
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
        val bingoItem = findBingoItem(bingoItemId)
        bingoItem.cancel(memberId)
        val afterBingoCount = bingoMap.calculateTotalBingoCount()
        if (afterBingoCount < beforeBingoCount) {
            registerEvent(
                BingoLineCancelEvent(
                    bingoBoardId = id,
                    bingoBoardTitle = title,
                    bingoItemTitle = bingoItem.title!!,
                    memberId = memberId,
                    otherMemberIds = bingoMembers.filter { it.memberId != memberId }.map { it.memberId }
                )
            )
        }
    }

    fun shuffleBingoItems() {
        if (isStarted()) {
            throw BingoInputException("임시 빙고가 아니면 shuffle 할 수 없습니다. BingoBoard Id : $id")
        }
        bingoItems.shuffled(Random)
            .forEachIndexed { index, bingoItem ->
                bingoItem.changeItemOrder(index + 1)
            }
        initBingoItemColor()
    }

    fun isLeader(memberId: Long): Boolean {
        return bingoMembers.any { bingoMember -> bingoMember.memberId == memberId && bingoMember.isLeader() }
    }

    fun delete() {
        this.active = false
    }

    fun validateViewableBy(memberId: Long) {
        if (!isLeader(memberId) && !isStarted() && getBingoMember(memberId).active) {
            throw BingoInputException("접근할 수 없는 빙고보드입니다.")
        }
    }

    fun validateCanLeave(memberId: Long) {
        if (isLeader(memberId)) {
            throw BingoIllegalStateException("그룹 빙고 리더는 빙고를 나갈 수 없습니다.")
        }
    }

    fun inactiveByMemberId(memberId: Long) {
        val bingoMember = getBingoMember(memberId)
        bingoMember.inactive()

        val completeMembers = getBingoCompleteMember(memberId)
        completeMembers.forEach { it?.inactive() }
    }

    private fun getBingoMember(memberId: Long): BingoMember {
        return bingoMembers.find { bingoMember -> bingoMember.memberId == memberId }
            ?: throw BingoIllegalStateException("해당 빙고보드에 포함되지 않는 회원입니다.")
    }

    private fun getBingoCompleteMember(memberId: Long): List<BingoCompleteMember?> {
        return bingoItems.map { bingoItem -> bingoItem.getBingoCompleteMember(memberId) }
    }

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
            val numberRange = BINGO_ITEM_ALPHABETS.shuffled().toMutableList()
            val size = BingoSize.cache(bingoSize)
            val board = BingoBoard(
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
                bingoMembers = mutableListOf(BingoMember.create(memberId, BingoMemberType.LEADER)),
                bingoItems = (1..(bingoSize * bingoSize)).map {
                    BingoItem.create(itemOrder = it, imageUrl = numberRange.removeAt(0))
                }
            )
            board.initBingoItemColor()
            return board
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
            bingoMembers: MutableList<BingoMember>,
            bingoItems: List<BingoItem>
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

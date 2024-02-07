package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoIllegalStateException
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import com.beside.groubing.groubingserver.global.domain.jpa.BaseAggregateRoot
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import kotlin.random.Random

@Entity
@Table(name = "BINGO_BOARDS")
class BingoBoard internal constructor(
    @Id
    @Column(name = "BINGO_BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    var title: String,

    @Enumerated(EnumType.STRING)
    val boardType: BingoBoardType,

    @Enumerated(EnumType.STRING)
    val bingoColor: BingoColor,

    var open: Boolean,

    var memo: String? = null,

    @Embedded
    private val bingoSize: BingoSize,

    @Embedded
    private var bingoGoal: BingoGoal,

    @Embedded
    private var period: BingoPeriod? = null,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_BOARD_ID")
    val bingoMembers: MutableList<BingoMember>,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_BOARD_ID")
    val bingoItems: List<BingoItem>

) : BaseAggregateRoot<BingoBoard>() {

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
        return bingoMembers.filter { it.memberId != memberId }
            .map { it.memberId }
    }

    fun isStarted(): Boolean {
        return period != null && bingoItems.all { it.isUpdated() }
    }

    fun isFinished(): Boolean = calculateLeftDays() < 0

    fun updateBingoItem(memberId: Long, bingoItemId: Long, title: String, subTitle: String?): BingoItem {
        validateAuthority(memberId)
        val bingoItem = findBingoItem(bingoItemId)
        bingoItem.updateBingoItem(
            title = title,
            subTitle = subTitle
        )
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
        findBingoItem(bingoItemId).completeBingoItem(memberId)
        val afterBingoCount = bingoMap.calculateTotalBingoCount()
        registerBingoItemCompleteEvent(afterBingoCount, beforeBingoCount, memberId)
    }

    private fun registerBingoItemCompleteEvent(afterBingoCount: Int, beforeBingoCount: Int, memberId: Long) {
        if (afterBingoCount > beforeBingoCount) {
            registerEvent(BingoItemCompleteEvent(bingoBoardId = id,
                bingoBoardTitle = title,
                totalBingoCount = afterBingoCount,
                memberId = memberId,
                otherMemberIds = bingoMembers.filter { it.memberId != memberId }.map { it.memberId })
            )
            return
        }
        if (bingoGoal.isGoal(afterBingoCount)) {
            registerEvent(BingoCompleteEvent(bingoBoardId = id,
                bingoBoardTitle = title,
                memberId = memberId,
                otherMemberIds = bingoMembers.filter { it.memberId != memberId }.map { it.memberId })
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
        bingoItem.cancelBingoItem(memberId)
        val afterBingoCount = bingoMap.calculateTotalBingoCount()
        if (afterBingoCount < beforeBingoCount) {
            registerEvent(BingoItemCancelEvent(bingoBoardId = id,
                bingoBoardTitle = title,
                bingoItemTitle = bingoItem.title!!,
                memberId = memberId,
                otherMemberIds = bingoMembers.filter { it.memberId != memberId }.map { it.memberId })
            )
        }
    }

    fun shuffleBingoItems() {
        if (isStarted()) {
            throw BingoInputException("임시 빙고가 아니면 shuffle 할 수 없습니다. BingoBoard Id : $id")
        }
        bingoItems.shuffled(Random)
            .forEachIndexed { index, bingoItem ->
                bingoItem.apply { bingoItem.changeItemOrder(index + 1) }
            }
    }

    fun isLeader(memberId: Long): Boolean {
        return bingoMembers.any { bingoMember -> bingoMember.memberId == memberId && bingoMember.isLeader() }
    }

    companion object {
        fun create(
            memberId: Long,
            title: String,
            goal: Int,
            boardType: BingoBoardType,
            open: Boolean,
            bingoSize: Int
        ): BingoBoard {
            val bingoItemAlphabets = listOf(
                "g", "r", "o", "u", "b", "i", "n", "o2", "i2",
                "g", "r", "o", "u", "b", "i", "n"
            )
            val numberRange = bingoItemAlphabets.shuffled().toMutableList()
            return BingoBoard(
                title = title,
                boardType = boardType,
                open = open,
                bingoSize = BingoSize.cache(bingoSize),
                bingoColor = BingoColor.makeRandomBingoColor(),
                bingoGoal = BingoGoal.create(goal, BingoSize.cache(bingoSize)),
                bingoMembers = mutableListOf(BingoMember.create(memberId, BingoMemberType.LEADER)),
                bingoItems = (1..(bingoSize * bingoSize)).map {
                    BingoItem.create(
                        it,
                        imageUrl = numberRange.removeAt(0)
                    )
                }
            )
        }
    }
}



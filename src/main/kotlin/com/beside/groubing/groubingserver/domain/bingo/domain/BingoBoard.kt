package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.QBingoMember.bingoMember
import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoIllegalStateException
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoItemUpdateCommand
import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import org.springframework.data.jpa.domain.AbstractPersistable_.id

@Entity
@Table(name = "BINGO_BOARDS")
class BingoBoard internal constructor(
    @Id
    @Column(name = "BINGO_BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    var title: String,

    val boardType: BingoBoardType,

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

) : BaseEntity() {

    val size: Int
        get() = bingoSize.size

    val goal: Int
        get() = bingoGoal.goal

    fun calculateLeftDays(): Long = period?.calculateLeftDays() ?: 0L

    fun makeBingoMap(memberId: Long): BingoMap {
        return BingoMap(memberId, size, bingoItems.sortedBy { it.itemOrder })
    }

    fun isStarted(): Boolean = period != null

    fun isFinished(): Boolean = calculateLeftDays() < 0

    fun updateBingoItem(memberId: Long, bingoItemId: Long, bingoItemUpdateCommand: BingoItemUpdateCommand): BingoItem {
        validateUpdateAuthority(memberId)
        val bingoItem = bingoItems.find { it.id == bingoItemId }
            ?: throw BingoIllegalStateException("해당 빙고를 수정할 권한이 없습니다.")
        bingoItem.updateBingoItem(
            title = bingoItemUpdateCommand.title,
            subTitle = bingoItemUpdateCommand.subTitle
        )
        return bingoItem
    }

    fun updateBase(memberId: Long, title: String, goal: Int, since: LocalDate, until: LocalDate) {
        validateUpdateAuthority(memberId)
        this.title = title
        this.bingoGoal = BingoGoal.create(goal, bingoSize)
        this.period = BingoPeriod.create(since, until)
    }

    fun updateBingoMembersPeriod(memberId: Long, bingoMembers: List<Long>, since: LocalDate, until: LocalDate) {
        validateUpdateAuthority(memberId)
        this.period = BingoPeriod.create(since, until)
        this.bingoMembers.addAll(bingoMembers.map { BingoMember.create(it) })
    }

    fun updateBingoMemo(memberId: Long, memo: String?) {
        validateUpdateAuthority(memberId)
        this.memo = memo
    }

    fun updateBingoOpen(memberId: Long, open: Boolean) {
        validateUpdateAuthority(memberId)
        this.open = open
    }

    private fun validateUpdateAuthority(memberId: Long) {
        val bingoMember = bingoMembers.find { it.memberId == memberId }
            ?: throw BingoInputException("해당 그룹 빙고에 포함되지 않은 빙고 memberId입니다. bingoBoardId: $id, memberId: $memberId")
        if (!bingoMember.isLeader()) {
            throw BingoIllegalStateException("해당 빙고를 수정할 권한이 없습니다.")
        }
    }

    fun completeBingoItem(bingoItemId: Long, memberId: Long) {
        bingoItems.find { it.id == bingoItemId }
            ?.completeBingoItem(memberId)
    }

    companion object {
        fun create(
            memberId: Long,
            title: String,
            goal: Int,
            boardType: BingoBoardType,
            open: Boolean,
            bingoSize: Int
        ): BingoBoard = BingoBoard(
            title = title,
            boardType = boardType,
            open = open,
            bingoSize = BingoSize.cache(bingoSize),
            bingoGoal = BingoGoal.create(goal, BingoSize.cache(bingoSize)),
            bingoMembers = mutableListOf(BingoMember.create(memberId, BingoMemberType.LEADER)),
            bingoItems = (1..(bingoSize * bingoSize)).map { BingoItem.create(it) }
        )
    }
}

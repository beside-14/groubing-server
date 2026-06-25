package com.beside.groubing.domain.bingo.entity

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.BingoBoardType
import com.beside.groubing.domain.bingo.domain.BingoColor
import com.beside.groubing.domain.bingo.domain.BingoGoal
import com.beside.groubing.domain.bingo.domain.BingoItems
import com.beside.groubing.domain.bingo.domain.BingoMember
import com.beside.groubing.domain.bingo.domain.BingoMembers
import com.beside.groubing.domain.bingo.domain.BingoPeriod
import com.beside.groubing.domain.bingo.exception.BingoInputException
import com.beside.groubing.global.domain.jpa.BaseAggregateRoot
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

@Entity
@Table(name = "BINGO_BOARDS")
class BingoBoardEntity(
    title: String,

    @Enumerated(EnumType.STRING)
    val boardType: BingoBoardType,

    @Enumerated(EnumType.STRING)
    val bingoColor: BingoColor,

    open: Boolean,

    memo: String? = null,

    active: Boolean = true,

    @Embedded
    val bingoSize: BingoSizeEmbeddable,

    bingoGoal: BingoGoalEmbeddable,

    period: BingoPeriodEmbeddable? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "BINGO_BOARD_ID")
    val bingoMembers: MutableList<BingoMemberEntity>,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "BINGO_BOARD_ID")
    val bingoItems: MutableList<BingoItemEntity>

) : BaseAggregateRoot<BingoBoardEntity>() {
    @Id
    @Column(name = "BINGO_BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var title: String = title
        private set

    var open: Boolean = open
        private set

    var memo: String? = memo
        private set

    var active: Boolean = active
        private set

    @Embedded
    var bingoGoal: BingoGoalEmbeddable = bingoGoal
        private set

    @Embedded
    var period: BingoPeriodEmbeddable? = period
        private set

    fun changeBase(title: String, bingoGoal: BingoGoal, period: BingoPeriod) {
        this.title = title
        this.bingoGoal = BingoGoalEmbeddable.from(bingoGoal)
        this.period = BingoPeriodEmbeddable.from(period)
    }

    fun addBingoMembers(bingoMembers: List<BingoMember>) {
        bingoMembers.forEach { this.bingoMembers.add(BingoMemberEntity.from(it)) }
    }

    fun changePeriod(period: BingoPeriod) {
        this.period = BingoPeriodEmbeddable.from(period)
    }

    fun changeMemo(memo: String?) {
        this.memo = memo
    }

    fun changeOpen(open: Boolean) {
        this.open = open
    }

    fun deactivateMember(memberId: Long) {
        bingoMembers.filter { it.memberId == memberId }.forEach { it.deactivate() }
        bingoItems.forEach { item ->
            item.completeMembers.filter { it.memberId == memberId }.forEach { it.deactivate() }
        }
    }

    fun changeLeader(newLeaderId: Long) {
        val newLeader = bingoMembers.find { it.memberId == newLeaderId }
            ?: throw BingoInputException("이양 대상 멤버가 빙고에 없습니다. bingoBoardId: $id, memberId: $newLeaderId")
        bingoMembers.filter { it.bingoMemberType.isLeader() }.forEach { it.demoteToParticipant() }
        newLeader.promoteToLeader()
    }

    fun applyChanges(domain: BingoBoard) {
        this.title = domain.title
        this.open = domain.open
        this.memo = domain.memo
        this.active = domain.active
        this.bingoGoal = BingoGoalEmbeddable.from(domain.bingoGoal)
        this.period = domain.period?.let { BingoPeriodEmbeddable.from(it) }
    }

    fun publishEvent(event: Any) {
        this.andEvent(event)
    }

    fun toDomain(): BingoBoard = BingoBoard.of(
        id = id,
        title = title,
        boardType = boardType,
        bingoColor = bingoColor,
        open = open,
        memo = memo,
        active = active,
        bingoSize = bingoSize.toDomain(),
        bingoGoal = bingoGoal.toDomain(bingoSize.toDomain()),
        period = period?.toDomain(),
        bingoMembers = BingoMembers.of(bingoMembers.map { it.toDomain() }.toMutableList()),
        bingoItems = BingoItems.of(bingoItems.map { it.toDomain() })
    )

    companion object {
        fun from(domain: BingoBoard): BingoBoardEntity {
            return BingoBoardEntity(
                title = domain.title,
                boardType = domain.boardType,
                bingoColor = domain.bingoColor,
                open = domain.open,
                memo = domain.memo,
                active = domain.active,
                bingoSize = BingoSizeEmbeddable.from(domain.bingoSize),
                bingoGoal = BingoGoalEmbeddable.from(domain.bingoGoal),
                period = domain.period?.let { BingoPeriodEmbeddable.from(it) },
                bingoMembers = domain.bingoMembers.map { BingoMemberEntity.from(it) }.toMutableList(),
                bingoItems = domain.bingoItems.map { BingoItemEntity.from(it) }.toMutableList()
            )
        }
    }
}

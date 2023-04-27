package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
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

@Entity
@Table(name = "BINGO_BOARDS")
class BingoBoard internal constructor(
    @Id
    @Column(name = "BINGO_BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    var title: String,

    val boardType: BingoBoardType,

    val open: Boolean,

    var memo: String? = null,

    @Embedded
    private val bingoSize: BingoSize,

    @Embedded
    private val bingoGoal: BingoGoal,

    @Embedded
    private var period: BingoPeriod? = null,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_BOARD_ID")
    val bingoMembers: List<BingoMember>,

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
        return BingoMap(memberId, size, bingoItems)
    }

    fun isStarted(): Boolean = period != null

    fun isFinished(): Boolean = calculateLeftDays() < 0

    fun updateBingoItems(updateBingoItems: List<BingoItem>) {
        updateBingoItems.forEach { updateBingoItem ->
            bingoItems.find { bingoItem -> bingoItem.isBingoItem(updateBingoItem.itemOrder) }
                ?.updateTitleAndSubTitle(updateBingoItem.title, updateBingoItem.subTitle)
        }
    }

    fun completeBingoItem(bingoItemId: Long, memberId: Long) {
        bingoItems.find { bingoItem -> bingoItem.isSame(bingoItemId) }
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
            bingoMembers = listOf(BingoMember.createBingoMember(memberId, BingoMemberType.LEADER)),
            bingoItems = (1..(bingoSize * bingoSize)).map { BingoItem.create(it) }
        )
    }
}

package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.embedded.BingoPeriod
import com.beside.groubing.groubingserver.domain.bingo.domain.embedded.BingoSizeAndGoal
import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoMemberFindException
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

@Entity
@Table(name = "BINGO_BOARDS")
class BingoBoard private constructor(
    @Id
    @Column(name = "BINGO_BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    var title: String,

    val boardType: BingoBoardType,

    var open: Boolean,

    var memo: String? = null,

    @Embedded
    private var sizeAndGoal: BingoSizeAndGoal,

    @Embedded
    private var period: BingoPeriod,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_BOARD_ID")
    val bingoMembers: List<BingoMember>,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_BOARD_ID")
    val bingoItems: List<BingoItem>

) : BaseEntity() {
    fun calculateLeftDays(): Long = period.calculateLeftDays()

    fun makeBingoMap(memberId: Long): BingoMap {
        return BingoMap(memberId, sizeAndGoal.bingoSize, bingoItems)
    }

    fun edit(title: String?, goal: Int?, open: Boolean?, since: LocalDate?, until: LocalDate?, memo: String?) {
        if (!title.isNullOrBlank()) this.title = title
        if (goal != null) this.sizeAndGoal = BingoSizeAndGoal.createBingoSizeAndGoal(this.bingoSize, goal)
        if (open != null) this.open = open
        if (memo != null) this.memo = memo
        when {
            since != null && until != null -> this.period = BingoPeriod.createBingoPeriod(since, until)
            since != null && until == null -> this.period = BingoPeriod.createBingoPeriod(since, this.until)
            since == null && until != null -> this.period = BingoPeriod.createBingoPeriod(this.since, until)
        }
    }

    fun isLeader(memberId: Long): Boolean = leader.memberId == memberId

    val leader: BingoMember
        get() = bingoMembers.find { it.bingoMemberType == BingoMemberType.LEADER }
            ?: throw BingoMemberFindException("빙고 생성자를 찾을 수 없습니다.")

    val participants: MutableList<BingoMember>
        get() = bingoMembers.filter { it.bingoMemberType == BingoMemberType.PARTICIPANT }.toMutableList()

    val bingoSize: Int
        get() = sizeAndGoal.bingoSize

    val goal: Int
        get() = sizeAndGoal.goal

    val since: LocalDate
        get() = period.since

    val until: LocalDate
        get() = period.until

    companion object {
        fun createBingoBoard(
            memberId: Long,
            title: String,
            goal: Int,
            boardType: BingoBoardType,
            open: Boolean,
            since: LocalDate,
            until: LocalDate,
            bingoSize: Int
        ): BingoBoard = BingoBoard(
            title = title,
            boardType = boardType,
            open = open,
            sizeAndGoal = BingoSizeAndGoal.createBingoSizeAndGoal(bingoSize, goal),
            period = BingoPeriod.createBingoPeriod(since, until),
            bingoMembers = listOf(BingoMember.createBingoMember(memberId, BingoMemberType.LEADER)),
            bingoItems = BingoItem.createBingoItems(bingoSize)
        )
    }
}

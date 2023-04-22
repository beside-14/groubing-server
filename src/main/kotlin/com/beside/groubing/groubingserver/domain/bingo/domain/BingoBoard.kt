package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
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

@Entity
@Table(name = "BINGO_BOARDS")
class BingoBoard private constructor(
    @Id
    @Column(name = "BINGO_BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val boardType: BingoBoardType,

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
    val bingoItems: List<BingoItem>,

    title: String,

    open: Boolean
) : BaseEntity() {

    var title: String = title
        private set

    var open: Boolean = open
        private set

    var memo: String? = null
        private set

    val size: Int
        get() = bingoSize.size

    val goal: Int
        get() = bingoGoal.goal

    val leader: BingoMember
        get() = bingoMembers.find { it.bingoMemberType == BingoMemberType.LEADER }
            ?: throw BingoMemberFindException("빙고 생성자를 찾을 수 없습니다.")

    val participants: MutableList<BingoMember>
        get() = bingoMembers.filter { it.bingoMemberType == BingoMemberType.PARTICIPANT }.toMutableList()

    fun calculateLeftDays(): Long = period?.calculateLeftDays() ?: 0L

    fun makeBingoMap(memberId: Long): BingoMap = BingoMap(memberId, size, bingoItems)

    fun editBoard(title: String?, goal: Int?) {
        if (!title.isNullOrBlank()) this.title = title
        if (goal != null) this.bingoGoal.edit(goal, this.bingoSize)
    }

    fun editMemo(memo: String?) {
        this.memo = memo
    }

    fun editOpenable(open: Boolean) {
        this.open = open
    }

    fun editItem(bingoItemId: Long, title: String, subTitle: String?, imageUrl: String?) {
        val item = findItem(bingoItemId)
        item.edit(title, subTitle, imageUrl)
    }

    fun isLeader(memberId: Long): Boolean = leader.memberId == memberId

    fun isStarted(): Boolean = period == null

    fun isFinished(): Boolean = calculateLeftDays() < 0

    fun findItem(bingoItemId: Long): BingoItem =
        bingoItems.find { it.id == bingoItemId } ?: throw BingoInputException("존재하지 않는 BingoItem Id입니다.")

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
            bingoItems = (0 until (bingoSize * bingoSize)).map { BingoItem.create() }
        )
    }
}

package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
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

@Entity
@Table(name = "BINGO_BOARDS")
class BingoBoard(
    @Id
    @Column(name = "BINGO_BOARD_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    var title: String,

    @Enumerated(EnumType.STRING)
    val type: BingoType,

    @Enumerated(EnumType.STRING)
    val size: BingoSize,

    @Enumerated(EnumType.STRING)
    val color: BingoColor,

    var goal: Int,

    val open: Boolean,

    var since: LocalDate,

    var until: LocalDate,

    var memo: String = "",

    creatorId: Long
) : BaseEntity() {

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_BOARD_ID")
    private var items: List<BingoItem>

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_BOARD_ID")
    var members: List<BingoMember>

    init {
        // 빙고 생성자 생성
        val bingoCreator =
            BingoMember(type = BingoMemberType.CREATOR, bingoBoard = this, member = Member(id = creatorId))
        this.members = listOf(bingoCreator)

        // 신규 아이템 생성
        val newItems = (0 until size.x).map { x ->
            (0 until size.y).map { y ->
                BingoItem(board = this, positionX = x, positionY = y)
            }
        }
        this.items = newItems.flatten()
    }

    fun getItems(): List<List<BingoItem>> = convertItems(items)

    fun setItems(items: List<List<BingoItem>>): List<List<BingoItem>> {
        this.items = items.flatten()
        return getItems()
    }

    fun updateBingoBoard(title: String, goal: Int, since: LocalDate, until: LocalDate, memo: String) {
        this.title = title
        this.goal = goal
        this.since = since
        this.until = until
        this.memo = memo
    }

    private fun convertItems(items: Collection<BingoItem>): List<List<BingoItem>> {
        val xToY = items.groupBy { item -> item.positionX }.toSortedMap()
        return xToY.values.toList()
    }
}

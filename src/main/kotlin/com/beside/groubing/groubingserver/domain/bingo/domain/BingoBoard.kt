package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "bingo_boards")
class BingoBoard(
    @Id
    @Column(name = "bingo_board_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val title: String = "",

    @Enumerated(EnumType.STRING)
    val type: BingoType = BingoType.PERSONAL,

    @Enumerated(EnumType.STRING)
    val size: BingoSize = BingoSize.NINE,

    @Enumerated(EnumType.STRING)
    val color: BingoColor = BingoColor.RANDOM,

    val goal: Int = 1,

    val open: Boolean = true,

    val since: LocalDate = LocalDate.now(),

    val until: LocalDate = LocalDate.now().plusDays(14),

    val memo: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", updatable = false)
    val member: Member = Member()
) : BaseEntity() {

    @OneToMany(mappedBy = "board")
    private val items: Collection<BingoItem> = mutableListOf()

    fun getItems(): List<List<BingoItem>> = convertItems(items)

    fun setItems(items: List<List<BingoItem>>): List<List<BingoItem>> {
        (this.items as MutableList).clear()
        this.items += (items.flatten())
        return getItems()
    }

    fun createNewItems(): List<List<BingoItem>> {
        val newItems = (0 until size.x).map { x ->
            (0 until size.y).map { y ->
                BingoItem(board = this, positionX = x, positionY = y)
            }
        }
        setItems(newItems)
        return newItems
    }

    private fun convertItems(items: Collection<BingoItem>): List<List<BingoItem>> {
        val xToY = items.groupBy { item -> item.positionX }.toSortedMap()
        return xToY.values.toList()
    }
}

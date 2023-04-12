package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "BINGO_BOARDS")
class BingoBoard private constructor(
    @Id
    @Column(name = "BINGO_BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    var title: String,

    var goal: Int,

    val boardType: BingoBoardType,

    val open: Boolean,

    var since: LocalDate,

    var until: LocalDate,

    val bingoSize: Int,

    var memo: String? = null,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_BOARD_ID")
    val bingoMembers: List<BingoMember>,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_BOARD_ID")
    val bingoItems: List<BingoItem>

) : BaseEntity() {
    fun calculateLeftDays(): Long {
        return LocalDate.now().until(until, ChronoUnit.DAYS)
    }

    fun makeBingoMap(memberId: Long): BingoMap {
        return BingoMap(memberId, bingoSize, bingoItems)
    }

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
        ): BingoBoard {
            val leaderBingoMember = BingoMember.createBingoMember(memberId, BingoMemberType.LEADER)
            val bingoItems = BingoItem.createBingoItems(bingoSize)
            return BingoBoard(
                title = title,
                goal = goal,
                boardType = boardType,
                open = open,
                since = since,
                until = until,
                bingoSize = bingoSize,
                bingoMembers = listOf(leaderBingoMember),
                bingoItems = bingoItems
            )
        }
    }
}

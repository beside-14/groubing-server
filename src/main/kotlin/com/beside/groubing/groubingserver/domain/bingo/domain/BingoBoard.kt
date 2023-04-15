package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
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
    init {
        if (bingoSize != 3 && bingoSize != 4) {
            throw BingoInputException("빙고 사이즈는 3X3 혹은 4X4 사이즈만 가능합니다.")
        }
        if (bingoSize == 3 && goal !in (1..3)) {
            throw BingoInputException("목표는 3개 이내로 설정해 주세요.")
        }
        if (bingoSize == 4 && goal !in (1..4)) {
            throw BingoInputException("목표는 4개 이내로 설정해 주세요.")
        }
        if (until.isBefore(since)) {
            throw BingoInputException("빙고 종료일은 시작일보다 과거일 수 없습니다.")
        }
    }

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

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
class BingoBoard(
    @Id
    @Column(name = "BINGO_BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val title: String,

    val goal: Int,

    val boardType: BingoBoardType,

    val open: Boolean,

    val since: LocalDate,

    val until: LocalDate,

    val bingoSize: Int,

    val memo: String,

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
}

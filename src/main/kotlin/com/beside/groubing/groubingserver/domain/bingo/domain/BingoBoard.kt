package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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

    @Enumerated(EnumType.STRING)
    val type: BingoType = BingoType.PERSONAL,

    @Enumerated(EnumType.STRING)
    val size: BingoSize = BingoSize.NINE,

    @Enumerated(EnumType.STRING)
    val color: BingoColor = BingoColor.RANDOM,

    val open: Boolean,

    val since: LocalDate = LocalDate.now(),

    val until: LocalDate = LocalDate.now().plusDays(14),

    val memo: String = "",
) : BaseEntity() {

    @OneToMany(mappedBy = "board")
    val items: Collection<BingoItem> = mutableListOf()
}

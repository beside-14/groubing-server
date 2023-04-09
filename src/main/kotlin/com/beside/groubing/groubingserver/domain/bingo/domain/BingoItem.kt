package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "BINGO_ITEMS")
class BingoItem(
    @Id
    @Column(name = "BINGO_ITEM_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val title: String = "",

    val subtitle: String = "",

    val imageUrl: String = "",

    @Column(updatable = false)
    val positionX: Int = 0,

    @Column(updatable = false)
    val positionY: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BINGO_BOARD_ID", updatable = false)
    val board: BingoBoard
) : BaseEntity()

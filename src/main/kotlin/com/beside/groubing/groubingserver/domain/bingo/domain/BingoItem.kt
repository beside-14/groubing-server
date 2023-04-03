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
@Table(name = "bingo_items")
class BingoItem(
    @Id
    @Column(name = "bingo_item_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val title: String = "",

    val subtitle: String = "",

    val imageUrl: String = "",

    val positionX: Int = 0,

    val positionY: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bingo_board_id", updatable = false)
    val board: BingoBoard
) : BaseEntity()

package com.beside.groubing.groubingserver.domain.bingo.domain

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "BINGO_ITEMS")
class BingoItem private constructor(
    @Id
    @Column(name = "BINGO_ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    var title: String? = null,

    var subTitle: String? = null,

    var imageUrl: String? = null,

    @ElementCollection
    @CollectionTable(name = "BINGO_COMPLETE_MEMBERS", joinColumns = [JoinColumn(name = "BINGO_ITEM_ID")])
    val completeMembers: MutableSet<Long> = mutableSetOf()
) {
    fun isCompleted(memberId: Long): Boolean {
        return completeMembers.contains(memberId)
    }

    fun addBingoMember(memberId: Long) {
        completeMembers.add(memberId)
    }

    fun cancelBingoMember(memberId: Long) {
        completeMembers.remove(memberId)
    }

    companion object {
        fun createBingoItems(bingoSize: Int): List<BingoItem> =
            (0 until (bingoSize * bingoSize)).map { BingoItem() }
    }
}

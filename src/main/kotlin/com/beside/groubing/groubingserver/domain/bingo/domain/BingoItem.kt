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
class BingoItem internal constructor(
    @Id
    @Column(name = "BINGO_ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    var title: String? = null,

    var subTitle: String? = null,

    var imageUrl: String? = null,

    val itemOrder: Int,

    @ElementCollection
    @CollectionTable(name = "BINGO_COMPLETE_MEMBERS", joinColumns = [JoinColumn(name = "BINGO_ITEM_ID")])
    val completeMembers: MutableSet<Long> = mutableSetOf()
) {
    fun isCompleted(memberId: Long): Boolean {
        return completeMembers.contains(memberId)
    }

    fun completeBingoItem(memberId: Long) {
        completeMembers.add(memberId)
    }

    fun cancelBingoItem(memberId: Long) {
        completeMembers.remove(memberId)
    }

    fun updateBingoItem(title: String, subTitle: String?) {
        this.title = title
        this.subTitle = subTitle
    }

    companion object {
        fun create(itemOrder: Int): BingoItem {
            return BingoItem(itemOrder = itemOrder)
        }
    }
}

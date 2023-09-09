package com.beside.groubing.groubingserver.domain.bingo.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
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

    var itemOrder: Int,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_ITEM_ID")
    val completeMembers: MutableSet<BingoCompleteMember> = mutableSetOf()
) {
    fun isCompleted(memberId: Long): Boolean {
        return completeMembers.any { it.memberId == memberId }
    }

    fun completeBingoItem(memberId: Long) {
        if (isCompleted(memberId)) {
            throw IllegalStateException("이미 Complete된 BingoItem입니다. bingoItemId: $id, memberId: $memberId")
        }
        completeMembers.add(BingoCompleteMember.create(memberId))
    }

    fun cancelBingoItem(memberId: Long) {
        completeMembers.remove(completeMembers.find{ it.memberId == memberId})
    }

    fun updateBingoItem(title: String, subTitle: String?) {
        this.title = title
        this.subTitle = subTitle
    }

    fun changeItemOrder(itemOrder: Int) {
        this.itemOrder = itemOrder
    }

    fun isUpdated(): Boolean {
        return title != null
    }

    companion object {
        fun create(itemOrder: Int): BingoItem {
            return BingoItem(itemOrder = itemOrder)
        }
    }
}

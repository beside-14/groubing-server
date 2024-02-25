package com.beside.groubing.groubingserver.domain.bingo.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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

    var imageUrl: String,

    var itemOrder: Int,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "BINGO_ITEM_ID")
    val completeMembers: MutableSet<BingoCompleteMember> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BINGO_BOARD_ID")
    val bingoBoard: BingoBoard? = null
) {
    fun getImageUrl(memberId: Long): String {
        if (isCompleted(memberId)) {
            return "${imageUrl}_complete$EXTENSION"
        }
        return "$imageUrl$EXTENSION"
    }

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
        completeMembers.remove(completeMembers.find { it.memberId == memberId })
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
        const val EXTENSION = ".png"

        fun create(itemOrder: Int, imageUrl: String): BingoItem {
            return BingoItem(itemOrder = itemOrder, imageUrl = imageUrl)
        }
    }
}

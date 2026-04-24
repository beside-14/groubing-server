package com.beside.groubing.groubingserver.domain.bingo.entity

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem
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
class BingoItemEntity(
    title: String? = null,
    subTitle: String? = null,
    val imageUrl: String,
    itemOrder: Int,
    colorCode: String = DEFAULT_COLOR,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "BINGO_ITEM_ID")
    val completeMembers: MutableSet<BingoCompleteMemberEntity> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BINGO_BOARD_ID", insertable = false, updatable = false)
    val bingoBoard: BingoBoardEntity? = null
) {
    @Id
    @Column(name = "BINGO_ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var title: String? = title
        private set

    var subTitle: String? = subTitle
        private set

    var itemOrder: Int = itemOrder
        private set

    var colorCode: String = colorCode
        private set

    fun applyChanges(domain: BingoItem) {
        this.title = domain.title
        this.subTitle = domain.subTitle
        this.itemOrder = domain.itemOrder
        this.colorCode = domain.colorCode
    }

    fun toDomain(): BingoItem = BingoItem.of(
        id = id,
        title = title,
        subTitle = subTitle,
        imageUrl = imageUrl,
        itemOrder = itemOrder,
        colorCode = colorCode,
        completeMembers = completeMembers.map { it.toDomain() }.toMutableSet()
    )

    companion object {
        const val DEFAULT_COLOR = "#2787C9"

        fun from(domain: BingoItem): BingoItemEntity {
            return BingoItemEntity(
                title = domain.title,
                subTitle = domain.subTitle,
                imageUrl = domain.imageUrl,
                itemOrder = domain.itemOrder,
                colorCode = domain.colorCode,
                completeMembers = domain.completeMembers.map { BingoCompleteMemberEntity.from(it) }.toMutableSet()
            )
        }
    }
}

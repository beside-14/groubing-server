package com.beside.groubing.groubingserver.domain.bingo.domain

class BingoItem private constructor(
    val id: Long,
    title: String?,
    subTitle: String?,
    val imageUrl: String,
    itemOrder: Int,
    colorCode: String,
    completeMembers: MutableSet<BingoCompleteMember>
) {
    var title: String? = title
        private set

    var subTitle: String? = subTitle
        private set

    var itemOrder: Int = itemOrder
        private set

    var colorCode: String = colorCode
        private set

    val completeMembers: MutableSet<BingoCompleteMember> = completeMembers

    fun initItemColorCode(colorCode: String) {
        this.colorCode = colorCode
    }

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

    fun isUpdated(): Boolean = title != null

    fun getBingoCompleteMember(memberId: Long): BingoCompleteMember? {
        return completeMembers.find { completeMember -> completeMember.memberId == memberId }
    }

    companion object {
        const val EXTENSION = ".png"
        private const val DEFAULT_COLOR = "#2787C9"

        fun create(itemOrder: Int, imageUrl: String): BingoItem {
            return BingoItem(
                id = 0L,
                title = null,
                subTitle = null,
                imageUrl = imageUrl,
                itemOrder = itemOrder,
                colorCode = DEFAULT_COLOR,
                completeMembers = mutableSetOf()
            )
        }

        fun of(
            id: Long,
            title: String?,
            subTitle: String?,
            imageUrl: String,
            itemOrder: Int,
            colorCode: String,
            completeMembers: MutableSet<BingoCompleteMember>
        ): BingoItem {
            return BingoItem(
                id = id,
                title = title,
                subTitle = subTitle,
                imageUrl = imageUrl,
                itemOrder = itemOrder,
                colorCode = colorCode,
                completeMembers = completeMembers
            )
        }
    }
}

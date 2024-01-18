package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem

class BingoItemResponse private constructor(
    val id: Long,
    val title: String?,
    val subTitle: String?,
    val imageUrl: String?,
    val itemOrder: Int,
    val complete: Boolean
) {
    companion object {
        fun fromBingoItem(bingoItem: BingoItem, memberId: Long): BingoItemResponse {
            return BingoItemResponse(
                id = bingoItem.id,
                title = bingoItem.title,
                subTitle = bingoItem.subTitle,
                imageUrl = bingoItem.getImageUrl(memberId),
                complete = bingoItem.isCompleted(memberId),
                itemOrder = bingoItem.itemOrder
            )
        }
    }
}

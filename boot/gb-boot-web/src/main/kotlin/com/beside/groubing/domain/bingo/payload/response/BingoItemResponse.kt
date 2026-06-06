package com.beside.groubing.domain.bingo.payload.response

import com.beside.groubing.domain.bingo.domain.BingoItem
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

class BingoItemResponse private constructor(
    @EncryptId(ObfuscationType.BINGO_ITEM)
    val id: Long,
    val title: String?,
    val subTitle: String?,
    val imageUrl: String?,
    val itemOrder: Int,
    val colorCode: String,
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
                itemOrder = bingoItem.itemOrder,
                colorCode = bingoItem.colorCode
            )
        }
    }
}

package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem

class BingoItemResponse(
    item: BingoItem
) {
    val bingoBoardId: Long = item.board.id
    val bingoItemId: Long = item.id
    val title: String = item.title
    val subtitle: String = item.subtitle
    val imageUrl: String = item.imageUrl
    val positionX: Int = item.positionX
    val positionY: Int = item.positionY

    companion object {
        fun newInstances(items: List<BingoItem>): List<BingoItemResponse> =
            items.map { item -> BingoItemResponse(item) }
    }
}

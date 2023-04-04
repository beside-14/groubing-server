package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoColor
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoType
import java.time.LocalDate

class BingoResponse(
    board: BingoBoard,
    items: Collection<BingoItem>
) {
    val bingoBoardId: Long = board.id
    val title: String = board.title
    val type: BingoType = board.type
    val size: BingoSizeResponse = BingoSizeResponse(board.size)
    val color: BingoColorResponse = BingoColorResponse((board.color))
    val goal: Int = board.goal
    val since: LocalDate = board.since
    val until: LocalDate = board.until
    val memo: String = board.memo
    val items: Collection<BingoItemResponse> = items.map { item -> BingoItemResponse(item) }

    class BingoSizeResponse(size: BingoSize) {
        val name: String = size.name
        val x: Int = size.x
        val y: Int = size.y
        val description: String = size.description
    }

    class BingoColorResponse(color: BingoColor) {
        val name: String = color.name
        val hex: String = color.hex
        val description: String = color.description
    }

    class BingoItemResponse(item: BingoItem) {
        val bingoItemId: Long = item.id
        val title: String = item.title
        val subtitle: String = item.subtitle
        val imageUrl: String = item.imageUrl
        val positionX: Int = item.positionX
        val positionY: Int = item.positionY
    }
}

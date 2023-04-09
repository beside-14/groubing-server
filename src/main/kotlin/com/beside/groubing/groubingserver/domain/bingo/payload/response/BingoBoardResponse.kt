package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoColor
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoType
import com.beside.groubing.groubingserver.domain.member.domain.Member
import java.time.LocalDate

class BingoBoardResponse(
    board: BingoBoard
) {
    val bingoBoardId: Long = board.id
    val creator: BingoCreatorResponse = BingoCreatorResponse(board.member)
    val title: String = board.title
    val type: BingoType = board.type
    val size: BingoSizeResponse = BingoSizeResponse(board.size)
    val color: BingoColorResponse = BingoColorResponse((board.color))
    val goal: Int = board.goal
    val open: Boolean = board.open
    val since: LocalDate = board.since
    val until: LocalDate = board.until
    val memo: String = board.memo
    val items: List<List<BingoItemResponse>> =
        board.getItems().map { bingoItems -> BingoItemResponse.newInstances(bingoItems) }

    class BingoCreatorResponse(member: Member) {
        val id: Long = member.id
        val email: String = member.email
    }

    class BingoSizeResponse(size: BingoSize) {
        val name: String = size.name
        val x: Int = size.x
        val y: Int = size.y
        val itemCount: Int = size.itemCount
        val description: String = size.description
    }

    class BingoColorResponse(color: BingoColor) {
        val name: String = color.name
        val hex: String = color.hex
        val description: String = color.description
    }
}

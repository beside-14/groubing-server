package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.exception.BingoIllegalStateException
import kotlin.random.Random

class BingoItems private constructor(val data: List<BingoItem>) : Iterable<BingoItem> {

    val size: Int
        get() = data.size

    operator fun get(index: Int): BingoItem = data[index]

    override fun iterator(): Iterator<BingoItem> = data.iterator()

    fun sortedByOrder(): List<BingoItem> = data.sortedBy { it.itemOrder }

    fun isAllUpdated(): Boolean = data.all { it.isUpdated() }

    fun findOf(itemId: Long): BingoItem =
        data.find { it.id == itemId }
            ?: throw BingoIllegalStateException("입력된 빙고 아이템 아이디가 잘못 되었습니다. id : $itemId")

    fun completeMembersOf(memberId: Long): List<BingoCompleteMember?> =
        data.map { it.getBingoCompleteMember(memberId) }

    fun shuffle() {
        data.shuffled(Random)
            .forEachIndexed { index, bingoItem -> bingoItem.changeItemOrder(index + 1) }
    }

    fun initColors(boardSize: Int) {
        val colors = BINGO_ITEM_COLORS.shuffled()

        sortedByOrder()
            .take(colors.size)
            .forEachIndexed { index, item -> item.initItemColorCode(colors[index]) }

        sortedByOrder()
            .drop(colors.size)
            .forEach { item ->
                val possible = colors.filter { color -> color !in neighborColorsOf(item.itemOrder - 1, boardSize) }
                item.initItemColorCode(possible.random())
            }
    }

    private fun neighborColorsOf(pos: Int, boardSize: Int): List<String?> {
        val neighbors = mutableListOf<String?>()
        val row = pos / boardSize
        val col = pos % boardSize

        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue
                val newRow = row + i
                val newCol = col + j
                if (newRow !in 0 until boardSize || newCol !in 0 until boardSize) continue
                val neighborIndex = newRow * boardSize + newCol + 1
                neighbors.add(data.first { it.itemOrder == neighborIndex }.colorCode)
            }
        }
        return neighbors
    }

    companion object {
        private val BINGO_ITEM_COLORS = listOf(
            "#2787C9", "#F18FA2", "#E75097", "#FFD643", "#B6B4DB", "#00AAB3", "#00A783", "#85BCE7", "#F6A973"
        )

        fun of(items: List<BingoItem>): BingoItems = BingoItems(items)

        fun create(boardSize: Int, alphabets: List<String>): BingoItems {
            val numberRange = alphabets.shuffled().toMutableList()
            val items = (1..(boardSize * boardSize)).map {
                BingoItem.create(itemOrder = it, imageUrl = numberRange.removeAt(0))
            }
            return BingoItems(items).also { it.initColors(boardSize) }
        }
    }
}

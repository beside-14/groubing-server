package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BingoMapTest {

    @Test
    fun `Test calculateTotalCompleteCount`() {
        val bingoItems = listOf(
            BingoItem(title = "1", subTitle = "1", imageUrl = ""),
            BingoItem(title = "2", subTitle = "2", imageUrl = ""),
            BingoItem(title = "3", subTitle = "3", imageUrl = ""),
            BingoItem(title = "4", subTitle = "4", imageUrl = ""),
            BingoItem(title = "5", subTitle = "5", imageUrl = ""),
            BingoItem(title = "6", subTitle = "6", imageUrl = ""),
            BingoItem(title = "7", subTitle = "7", imageUrl = ""),
            BingoItem(title = "8", subTitle = "8", imageUrl = ""),
            BingoItem(title = "9", subTitle = "9", imageUrl = "")
        )
        val memberId = 1L
        val bingoMap = BingoMap(memberId, 3, bingoItems)

        // no complete
        assertEquals(0, bingoMap.calculateTotalCompleteCount())

        // horizontal bingo
        bingoItems[0].completeMembers.add(memberId)
        bingoItems[1].completeMembers.add(memberId)
        bingoItems[2].completeMembers.add(memberId)
        assertEquals(1, bingoMap.calculateTotalCompleteCount())

        // vertical bingo
        bingoItems[3].completeMembers.add(memberId)
        bingoItems[6].completeMembers.add(memberId)
        assertEquals(2, bingoMap.calculateTotalCompleteCount())

        // diagonal bingo
        bingoItems[4].completeMembers.add(memberId)
        bingoItems[8].completeMembers.add(memberId)
        assertEquals(4, bingoMap.calculateTotalCompleteCount())

        // all complete
        bingoItems[1].completeMembers.add(memberId)
        bingoItems[2].completeMembers.add(memberId)
        bingoItems[3].completeMembers.add(memberId)
        bingoItems[4].completeMembers.add(memberId)
        bingoItems[5].completeMembers.add(memberId)
        bingoItems[6].completeMembers.add(memberId)
        bingoItems[7].completeMembers.add(memberId)
        bingoItems[8].completeMembers.add(memberId)
        assertEquals(8, bingoMap.calculateTotalCompleteCount())
    }
}

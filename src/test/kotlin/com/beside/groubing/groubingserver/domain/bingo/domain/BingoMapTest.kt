package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BingoMapTest {

    @Test
    fun `Test calculateTotalCompleteCount`() {
        val bingoSize = 3
        val bingoItems = BingoItem.createBingoItems(bingoSize)
        val memberId = 1L
        val bingoMap = BingoMap(memberId, bingoSize, bingoItems)

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

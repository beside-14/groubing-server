package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BingoMapTest {

    @Test
    fun `Test calculateTotalBingoCount`() {
        val bingoSize = 3
        val bingoItems = (0 until (bingoSize * bingoSize)).map { BingoItem.create(it) }
        val memberId = 1L
        val bingoMap = BingoMap(memberId, bingoSize, bingoItems)

        // no complete
        assertEquals(0, bingoMap.calculateTotalBingoCount())

        // horizontal bingo
        bingoItems[0].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[1].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[2].completeMembers.add(BingoCompleteMember.create(memberId))
        assertEquals(1, bingoMap.calculateTotalBingoCount())

        // vertical bingo
        bingoItems[3].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[6].completeMembers.add(BingoCompleteMember.create(memberId))
        assertEquals(2, bingoMap.calculateTotalBingoCount())

        // diagonal bingo
        bingoItems[4].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[8].completeMembers.add(BingoCompleteMember.create(memberId))
        assertEquals(4, bingoMap.calculateTotalBingoCount())

        // all complete
        bingoItems[1].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[2].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[3].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[4].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[5].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[6].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[7].completeMembers.add(BingoCompleteMember.create(memberId))
        bingoItems[8].completeMembers.add(BingoCompleteMember.create(memberId))
        assertEquals(8, bingoMap.calculateTotalBingoCount())
    }
}

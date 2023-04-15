package com.beside.groubing.groubingserver.domain.bingo.domain.embedded

import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import jakarta.persistence.Embeddable

@Embeddable
class BingoSizeAndGoal(
    var bingoSize: Int,
    var goal: Int
) {
    init {
        if (bingoSize != 3 && bingoSize != 4) throw BingoInputException("빙고 사이즈는 3X3 혹은 4X4 사이즈만 가능합니다.")
        if (bingoSize == 3 && goal !in (1..3)) throw BingoInputException("목표는 3개 이내로 설정해 주세요.")
        if (bingoSize == 4 && goal !in (1..4)) throw BingoInputException("목표는 4개 이내로 설정해 주세요.")
    }

    companion object {
        fun createBingoSizeAndGoal(bingoSize: Int, goal: Int): BingoSizeAndGoal = BingoSizeAndGoal(bingoSize, goal)
    }
}

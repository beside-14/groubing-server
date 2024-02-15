package com.beside.groubing.groubingserver.domain.bingo.dao

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import org.springframework.stereotype.Component

@Component
class BingoBoardFindDao(
    private val bingoBoardRepository: BingoBoardRepository
) {
    fun findById(id: Long): BingoBoard {
        return bingoBoardRepository.findByIdAndActiveIsTrue(id)
            .orElseThrow { BingoInputException("존재하지 않는 BingoBoard Id입니다. : $id") }
    }
}

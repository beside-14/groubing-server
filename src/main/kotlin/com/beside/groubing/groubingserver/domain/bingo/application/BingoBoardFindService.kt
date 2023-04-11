package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domagin.bingo.dto.BingoBoardResponse
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardFindService(
    private val bingoBoardRepository: BingoBoardRepository
) {
    fun findBingoBoard(memberId: Long, boardId: Long): BingoBoardResponse {
        val bingoBoard = bingoBoardRepository.findById(boardId)
            .orElseThrow { throw BingoInputException("존재하지 않는 BingoBoard Id입니다. : $boardId") }
        return BingoBoardResponse.fromBingoBoard(bingoBoard, memberId)
    }
}

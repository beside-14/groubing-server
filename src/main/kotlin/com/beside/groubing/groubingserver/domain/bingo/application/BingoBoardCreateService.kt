package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardCreateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoCreateService(
    private val bingoBoardRepository: BingoBoardRepository
) {
    fun create(command: BingoBoardCreateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardRepository.save(command.toNewBingoBoard())
        return BingoBoardResponse.fromBingoBoard(bingoBoard, command.memberId)
    }
}

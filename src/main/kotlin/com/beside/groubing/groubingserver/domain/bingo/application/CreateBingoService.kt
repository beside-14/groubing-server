package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.domain.bingo.payload.command.CreateBingoCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateBingoService(
    private val boardRepository: BingoBoardRepository
) {
    /**
     * 신규 빙고 보드 생성
     */
    @Transactional
    fun create(command: CreateBingoCommand): BingoBoardResponse {
        // 빙고 보드 기본 정보 생성
        val board = command.toBingo()
        boardRepository.save(board)
        return BingoBoardResponse(board)
    }
}

package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardMapper
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItemRepository
import com.beside.groubing.groubingserver.domain.bingo.payload.command.CreateBingoCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateBingoService(
    private val boardRepository: BingoBoardRepository,
    private val itemRepository: BingoItemRepository,
    private val boardMapper: BingoBoardMapper
) {

    /**
     * 신규 빙고 보드 생성
     */
    @Transactional
    fun create(command: CreateBingoCommand): BingoBoardResponse {
        // 빙고 보드 기본 정보 생성
        val board = boardRepository.save(boardMapper.toBingoBoard(command))
        // 기본 빙고 항목 생성
        itemRepository.saveAll(board.createNewItems().flatten())
        return BingoBoardResponse(board)
    }
}

package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoEditException
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardEditCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoBoardEditService(
    private val bingoBoardRepository: BingoBoardRepository
) {
    fun edit(command: BingoBoardEditCommand): BingoBoardResponse {
        val board = bingoBoardRepository.findById(command.id)
            .orElseThrow { BingoInputException("존재하지 않는 BingoBoard Id입니다. : ${command.id}") }
        if (board.getLeader().isMe(command.memberId)) throw BingoEditException("해당 빙고를 수정할 권한이 없습니다.")
        board.edit(command.title, command.goal, command.since, command.since)
        return BingoBoardResponse.fromBingoBoard(board, command.memberId)
    }
}

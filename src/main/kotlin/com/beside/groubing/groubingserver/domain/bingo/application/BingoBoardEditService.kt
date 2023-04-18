package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
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
    fun editBoard(command: BingoBoardEditCommand): BingoBoardResponse {
        val board = getBoard(command.memberId, command.id)
        board.editBoard(command.title, command.goal)
        return BingoBoardResponse.fromBingoBoard(board, command.memberId)
    }

    fun editMemo(memberId: Long, bingoBoardId: Long, memo: String?): BingoBoardResponse {
        val board = getBoard(memberId, bingoBoardId)
        board.editMemo(memo)
        return BingoBoardResponse.fromBingoBoard(board, memberId)
    }

    fun editOpenable(memberId: Long, bingoBoardId: Long, open: Boolean): BingoBoardResponse {
        val board = getBoard(memberId, bingoBoardId)
        board.editOpenable(open)
        return BingoBoardResponse.fromBingoBoard(board, memberId)
    }

    private fun getBoard(memberId: Long, bingoBoardId: Long): BingoBoard {
        val board = bingoBoardRepository.findById(bingoBoardId)
            .orElseThrow { BingoInputException("존재하지 않는 BingoBoard Id입니다. : $bingoBoardId") }
        if (!board.isLeader(memberId)) throw BingoEditException("해당 빙고를 수정할 권한이 없습니다.")
        return board
    }
}

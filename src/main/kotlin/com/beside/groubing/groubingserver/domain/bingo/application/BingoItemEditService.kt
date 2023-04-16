package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoEditException
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoItemEditCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoItemEditService(
    private val bingoBoardRepository: BingoBoardRepository
) {
    fun edit(command: BingoItemEditCommand): BingoBoardResponse.BingoItemResponse {
        val board = bingoBoardRepository.findById(command.bingoBoardId)
            .orElseThrow { BingoInputException("존재하지 않는 BingoBoard Id입니다. : ${command.bingoBoardId}") }
        if (!board.isLeader(command.memberId)) throw BingoEditException("해당 빙고를 수정할 권한이 없습니다.")
        board.editItem(command.id, command.title, command.subTitle, command.imageUrl)
        val item = board.findItem(command.id)
        return BingoBoardResponse.BingoItemResponse.fromBingoItem(item, command.memberId)
    }
}

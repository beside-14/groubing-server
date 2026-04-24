package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardBaseUpdateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardMembersPeriodUpdateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardMemoUpdateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardOpenUpdateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoBoardUpdateService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun updateBase(bingoBoardId: Long, memberId: Long, command: BingoBoardBaseUpdateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        command.update(bingoBoard, memberId)
        val updated = bingoBoardCommandRepository.update(bingoBoard)
        return BingoBoardResponse.fromBingoBoard(updated, memberId)
    }

    fun updateMembersPeriod(bingoBoardId: Long, memberId: Long, command: BingoBoardMembersPeriodUpdateCommand): BingoBoardResponse {
        validateExistingMembers(command.bingoMembers)
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        command.update(bingoBoard, memberId)
        val updated = bingoBoardCommandRepository.update(bingoBoard)
        return BingoBoardResponse.fromBingoBoard(updated, memberId)
    }

    fun updateMemo(bingoBoardId: Long, memberId: Long, command: BingoBoardMemoUpdateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        command.update(bingoBoard, memberId)
        val updated = bingoBoardCommandRepository.update(bingoBoard)
        return BingoBoardResponse.fromBingoBoard(updated, memberId)
    }

    fun updateOpen(bingoBoardId: Long, memberId: Long, command: BingoBoardOpenUpdateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardQueryRepository.findOne(bingoBoardId)
        command.update(bingoBoard, memberId)
        val updated = bingoBoardCommandRepository.update(bingoBoard)
        return BingoBoardResponse.fromBingoBoard(updated, memberId)
    }

    private fun validateExistingMembers(memberIds: List<Long>) {
        if (memberQueryRepository.countByIdIn(memberIds) != memberIds.size) {
            throw MemberInputException("입력된 ID 중 존재하지 않는 회원이 있습니다. memberIds:$memberIds")
        }
    }
}

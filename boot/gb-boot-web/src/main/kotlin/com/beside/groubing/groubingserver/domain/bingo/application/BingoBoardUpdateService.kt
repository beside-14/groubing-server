package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
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
    private val bingoBoardFindDao: BingoBoardFindDao,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun updateBase(bingoBoardId: Long, memberId: Long, baseUpdateCommand: BingoBoardBaseUpdateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        baseUpdateCommand.update(bingoBoard, memberId)
        return BingoBoardResponse.fromBingoBoard(bingoBoard, memberId)
    }

    fun updateMembersPeriod(bingoBoardId: Long, memberId: Long, membersPeriodUpdateCommand: BingoBoardMembersPeriodUpdateCommand): BingoBoardResponse {
        validateExistingMembers(membersPeriodUpdateCommand.bingoMembers)
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        membersPeriodUpdateCommand.update(bingoBoard, memberId)
        return BingoBoardResponse.fromBingoBoard(bingoBoard, memberId)
    }

    fun updateMemo(bingoBoardId: Long, memberId: Long, memoUpdateCommand: BingoBoardMemoUpdateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        memoUpdateCommand.update(bingoBoard, memberId)
        return BingoBoardResponse.fromBingoBoard(bingoBoard, memberId)
    }

    fun updateOpen(bingoBoardId: Long, memberId: Long, openUpdateCommand: BingoBoardOpenUpdateCommand): BingoBoardResponse {
        val bingoBoard = bingoBoardFindDao.findById(bingoBoardId)
        openUpdateCommand.update(bingoBoard, memberId)
        return BingoBoardResponse.fromBingoBoard(bingoBoard, memberId)
    }

    private fun validateExistingMembers(memberIds: List<Long>) {
        if (memberQueryRepository.countByIdIn(memberIds) != memberIds.size) {
            throw MemberInputException("입력된 ID 중 존재하지 않는 회원이 있습니다. memberIds:$memberIds")
        }
    }
}

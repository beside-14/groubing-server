package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardDetailResponse
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.repository.MemberJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardFindService(
    private val bingoBoardFindDao: BingoBoardFindDao,
    private val memberJpaRepository: MemberJpaRepository
) {
    fun findBingoBoard(memberId: Long, boardId: Long): BingoBoardDetailResponse {
        val bingoBoard = bingoBoardFindDao.findById(boardId)
        bingoBoard.validateNotLeaderAndDraft(memberId)
        val otherMembers = memberJpaRepository.findAllById(bingoBoard.getOtherBingoMemberIds(memberId))
        val member = memberJpaRepository.findById(memberId)
            .orElseThrow { MemberInputException("존재하지 않는 유저 입니다.") }
        return BingoBoardDetailResponse.fromBingoBoard(bingoBoard, member, otherMembers)
    }
}

package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardDetailResponse
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardFindService(
    private val bingoBoardFindDao: BingoBoardFindDao,

    private val memberRepository: MemberRepository
) {
    fun findBingoBoard(memberId: Long, boardId: Long): BingoBoardDetailResponse {
        val bingoBoard = bingoBoardFindDao.findById(boardId)
        val (currentMember, otherMembers) = memberRepository.findAllById(bingoBoard.getBingoMemberIds())
            .partition { it.id == memberId }
        return BingoBoardDetailResponse.fromBingoBoard(bingoBoard, currentMember.first(), otherMembers)
    }
}

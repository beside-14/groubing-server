package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardDetailResponse
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardFindService(
    private val bingoBoardFindDao: BingoBoardFindDao,
    private val memberRepository: MemberRepository,
    private val memberFindDao: MemberFindDao
) {
    fun findBingoBoard(memberId: Long, boardId: Long): BingoBoardDetailResponse {
        val bingoBoard = bingoBoardFindDao.findById(boardId)
        bingoBoard.validateNotLeaderAndDraft(memberId)
        val otherMembers = memberRepository.findAllById(bingoBoard.getOtherBingoMemberIds(memberId))
        val member = memberFindDao.findExistingMemberById(memberId)
        return BingoBoardDetailResponse.fromBingoBoard(bingoBoard, member, otherMembers)
    }
}

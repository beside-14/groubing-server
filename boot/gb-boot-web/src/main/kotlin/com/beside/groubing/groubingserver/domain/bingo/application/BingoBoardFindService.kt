package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardDetailResponse
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardFindService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun findBingoBoard(memberId: Long, boardId: Long): BingoBoardDetailResponse {
        val bingoBoard = bingoBoardQueryRepository.findOne(boardId)
        bingoBoard.validateNotLeaderAndDraft(memberId)
        val otherMembers = memberQueryRepository.findAllByIdIn(bingoBoard.getOtherBingoMemberIds(memberId))
        val member = memberQueryRepository.findById(memberId)
        return BingoBoardDetailResponse.fromBingoBoard(bingoBoard, member, otherMembers)
    }
}

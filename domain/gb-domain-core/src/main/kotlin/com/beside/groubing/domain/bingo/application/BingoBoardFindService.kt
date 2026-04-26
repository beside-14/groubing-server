package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.BingoBoardDetail
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardFindService(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun findBingoBoard(memberId: Long, boardId: Long): BingoBoardDetail {
        val bingoBoard = bingoBoardQueryRepository.findOne(boardId)
        bingoBoard.validateNotLeaderAndDraft(memberId)
        val viewer = memberQueryRepository.findById(memberId)
        val otherMembers = memberQueryRepository.findAll(bingoBoard.getOtherBingoMemberIds(memberId))
        return BingoBoardDetail(bingoBoard, viewer, otherMembers)
    }
}

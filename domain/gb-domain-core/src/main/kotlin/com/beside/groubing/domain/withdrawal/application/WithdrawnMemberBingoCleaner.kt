package com.beside.groubing.domain.withdrawal.application

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.notification.domain.port.NotificationRepository
import org.springframework.stereotype.Component

@Component
class WithdrawnMemberBingoCleaner(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository,
    private val notificationRepository: NotificationRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    /**
     * 탈퇴 회원이 속한 빙고 보드를 정리한다.
     * - 살아있는 동료가 없는 보드(단독 또는 동료 전원 탈퇴)는 hard-delete.
     * - 살아있는 동료가 있고 탈퇴 회원이 리더면 리더십을 이양한다.
     *
     * @return 탈퇴 회원이 살아남은 그룹 빙고에 여전히 참여자로 남아있으면 true(= tombstone 필요).
     */
    fun cleanUpBoardsOf(memberId: Long): Boolean {
        return bingoBoardQueryRepository.findAllOf(memberId)
            .map { cleanUpBoard(it, memberId) }
            .any { it }
    }

    private fun cleanUpBoard(board: BingoBoard, memberId: Long): Boolean {
        val livingOtherIds = livingOtherMemberIds(board, memberId)
        if (livingOtherIds.isEmpty()) {
            deleteBoard(board.id)
            return false
        }
        if (board.isLeader(memberId)) {
            bingoBoardCommandRepository.changeLeader(board.id, livingOtherIds.min())
        }
        return true
    }

    private fun livingOtherMemberIds(board: BingoBoard, memberId: Long): List<Long> {
        val otherActiveIds = board.otherActiveMemberIdsOf(memberId)
        return memberQueryRepository.findAllActive(otherActiveIds).map { it.id }
    }

    private fun deleteBoard(bingoBoardId: Long) {
        notificationRepository.deleteAllByBingoBoardId(bingoBoardId)
        bingoBoardCommandRepository.hardDelete(bingoBoardId)
    }
}

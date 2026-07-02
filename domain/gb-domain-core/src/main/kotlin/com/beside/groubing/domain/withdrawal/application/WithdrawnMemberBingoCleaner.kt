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
        val boards = bingoBoardQueryRepository.findAllOf(memberId)
        val livingMemberIds = findLivingMemberIds(boards, memberId)
        return boards
            .map { cleanUpBoard(it, memberId, livingMemberIds) }
            .any { it }
    }

    private fun findLivingMemberIds(boards: List<BingoBoard>, memberId: Long): Set<Long> {
        val candidateIds = boards.flatMap { it.otherActiveMemberIdsOf(memberId) }.distinct()
        if (candidateIds.isEmpty()) return emptySet()
        return memberQueryRepository.findAllActive(candidateIds).map { it.id }.toSet()
    }

    private fun cleanUpBoard(board: BingoBoard, memberId: Long, livingMemberIds: Set<Long>): Boolean {
        val livingOtherIds = board.otherActiveMemberIdsOf(memberId).filter { it in livingMemberIds }
        if (livingOtherIds.isEmpty()) {
            deleteBoard(board.id)
            return false
        }
        if (board.isLeader(memberId)) {
            bingoBoardCommandRepository.changeLeader(board.id, board.electNextLeader(livingOtherIds))
        }
        return true
    }

    private fun deleteBoard(bingoBoardId: Long) {
        notificationRepository.deleteAllByBingoBoardId(bingoBoardId)
        bingoBoardCommandRepository.hardDelete(bingoBoardId)
    }
}

package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardFindDao
import com.beside.groubing.groubingserver.domain.bingo.exception.BingoIllegalStateException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoMemberLeaveService(
    private val bingoBoardFindDao: BingoBoardFindDao
) {
    fun leaveBingoBoard(memberId: Long, bingoId: Long) {
        val bingoBoard = bingoBoardFindDao.findById(bingoId)
        if (bingoBoard.isLeader(memberId)) {
            throw BingoIllegalStateException("그룹 빙고 리더는 빙고를 나갈 수 없습니다.")
        }
        bingoBoard.inactiveByMemberId(memberId)
    }
}

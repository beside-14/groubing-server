package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.bingo.exception.BingoIllegalStateException
import org.springframework.stereotype.Component

@Component
class BingoLeaderValidator(
    private val bingoBoardQueryRepository: BingoBoardQueryRepository
) {
    fun validate(bingoBoardId: Long, memberId: Long) {
        if (!bingoBoardQueryRepository.isLeaderOf(bingoBoardId, memberId)) {
            throw BingoIllegalStateException("해당 빙고를 수정할 권한이 없습니다.")
        }
    }
}

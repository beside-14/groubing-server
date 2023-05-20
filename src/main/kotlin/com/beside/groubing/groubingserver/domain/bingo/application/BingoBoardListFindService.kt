package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardOverviewResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BingoBoardListFindService(
    private val bingoBoardListFindDao: BingoBoardListFindDao
) {
    fun findBingoBoardList(memberId: Long): List<BingoBoardOverviewResponse> {
        return bingoBoardListFindDao.findBingoBoardList(memberId)
    }
}


package com.beside.groubing.groubingserver.domain.bingo.repository

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardCommandRepository
import org.springframework.stereotype.Repository

@Repository
class BingoBoardRepositoryAdapter(
    private val bingoBoardListFindDao: BingoBoardListFindDao
) : BingoBoardCommandRepository {
    override fun inactivateAllOf(memberId: Long) {
        bingoBoardListFindDao.findBingoBoardList(memberId)
            .forEach { it.inactiveByMemberId(memberId) }
    }
}

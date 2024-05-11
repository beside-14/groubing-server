package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WithdrawalService(
    private val memberFindDao: MemberFindDao,
    private val bingoBoardListFindDao: BingoBoardListFindDao
) {

    fun withdrawal(memberId: Long) {
        val member = memberFindDao.findExistingMemberById(memberId)
        member.withdrawal()

        val bingoBoardList = bingoBoardListFindDao.findBingoBoardList(memberId);
        bingoBoardList.map { it.inactiveByMemberId(memberId) }
    }
}

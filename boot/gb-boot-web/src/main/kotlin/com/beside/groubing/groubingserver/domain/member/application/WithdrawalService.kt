package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WithdrawalService(
    private val memberFindDao: MemberFindDao,
    private val memberCommandRepository: MemberCommandRepository,
    private val bingoBoardListFindDao: BingoBoardListFindDao
) {
    fun withdrawal(memberId: Long) {
        val member = memberFindDao.findExistingMemberById(memberId)
        memberCommandRepository.update(member.withdrawn())

        val bingoBoardList = bingoBoardListFindDao.findBingoBoardList(memberId)
        bingoBoardList.forEach { it.inactiveByMemberId(memberId) }
    }
}

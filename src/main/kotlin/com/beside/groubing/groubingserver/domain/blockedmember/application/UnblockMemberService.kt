package com.beside.groubing.groubingserver.domain.blockedmember.application

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberFindDao
import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UnblockMemberService(
    private val blockedMemberFindDao: BlockedMemberFindDao,
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun unblock(id: Long) {
        val blockedMember = blockedMemberFindDao.findById(id)
        blockedMemberRepository.delete(blockedMember)
    }
}

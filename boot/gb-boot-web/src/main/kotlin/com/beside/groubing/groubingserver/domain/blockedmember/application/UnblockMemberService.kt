package com.beside.groubing.groubingserver.domain.blockedmember.application

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberFindDao
import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberValidateDao
import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UnblockMemberService(
    private val blockedMemberFindDao: BlockedMemberFindDao,
    private val blockedMemberValidateDao: BlockedMemberValidateDao,
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun unblock(requesterId: Long, id: Long) {
        val blockedMember = blockedMemberFindDao.findById(id)
        blockedMemberValidateDao.validateUnblock(requesterId, blockedMember)
        blockedMemberRepository.delete(blockedMember)
    }
}

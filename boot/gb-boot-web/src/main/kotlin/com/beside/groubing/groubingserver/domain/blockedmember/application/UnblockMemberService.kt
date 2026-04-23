package com.beside.groubing.groubingserver.domain.blockedmember.application

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberValidateDao
import com.beside.groubing.groubingserver.domain.blockedmember.domain.port.BlockedMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UnblockMemberService(
    private val blockedMemberValidateDao: BlockedMemberValidateDao,
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun unblock(requesterId: Long, id: Long) {
        val blockedMember = blockedMemberRepository.findById(id)
        blockedMemberValidateDao.validateUnblock(requesterId, blockedMember)
        blockedMemberRepository.delete(blockedMember)
    }
}

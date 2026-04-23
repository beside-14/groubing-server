package com.beside.groubing.groubingserver.domain.blockedmember.application

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberValidateDao
import com.beside.groubing.groubingserver.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
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
            ?: throw FriendInputException("차단 내역이 존재하지 않습니다.")
        blockedMemberValidateDao.validateUnblock(requesterId, blockedMember)
        blockedMemberRepository.delete(blockedMember)
    }
}

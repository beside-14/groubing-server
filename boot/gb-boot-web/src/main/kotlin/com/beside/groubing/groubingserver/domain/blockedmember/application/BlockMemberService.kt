package com.beside.groubing.groubingserver.domain.blockedmember.application

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberValidateDao
import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.friend.dao.FriendDeleteDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlockMemberService(
    private val blockedMemberRepository: BlockedMemberRepository,
    private val blockedMemberValidateDao: BlockedMemberValidateDao,
    private val friendDeleteDao: FriendDeleteDao
) {
    fun block(requesterId: Long, targetMemberId: Long) {
        blockedMemberValidateDao.validateBlockMember(requesterId, targetMemberId)

        friendDeleteDao.deleteAll(requesterId, targetMemberId)

        blockedMemberRepository.save(BlockedMember.create(requesterId, targetMemberId))
    }
}

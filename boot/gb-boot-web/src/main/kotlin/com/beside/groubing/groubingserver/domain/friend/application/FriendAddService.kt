package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberValidateDao
import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRelations
import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendCommandRepository
import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendQueryRepository
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendAddService(
    private val blockedMemberValidateDao: BlockedMemberValidateDao,
    private val friendQueryRepository: FriendQueryRepository,
    private val friendCommandRepository: FriendCommandRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun add(inviterId: Long, inviteeId: Long) {
        blockedMemberValidateDao.validateEachOther(inviterId, inviteeId)
        validateMembersExist(inviterId, inviteeId)

        val friends = friendQueryRepository.findAllBetween(inviterId, inviteeId)
        if (friends.isEmpty()) {
            friendCommandRepository.save(Friend.create(inviterId, inviteeId))
            return
        }

        val rependable = FriendRelations(friends).findRependable(inviterId, inviteeId)
        friendCommandRepository.update(rependable.repend())
    }

    private fun validateMembersExist(inviterId: Long, inviteeId: Long) {
        memberQueryRepository.findById(inviterId)
        memberQueryRepository.findById(inviteeId)
    }
}

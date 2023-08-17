package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberValidateDao
import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.dao.FriendValidateDao
import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendAddService(
    private val blockedMemberValidateDao: BlockedMemberValidateDao,
    private val friendValidateDao: FriendValidateDao,
    private val friendFindDao: FriendFindDao,
    private val memberFindDao: MemberFindDao,
    private val friendRepository: FriendRepository
) {
    fun add(inviterId: Long, inviteeId: Long) {
        friendValidateDao.validateIsMe(inviterId, inviteeId)
        blockedMemberValidateDao.validateEachOther(inviterId, inviteeId)

        val friends = friendFindDao.findByFriends(inviterId, inviteeId)

        if (friends.isEmpty()) {
            val memberMap = memberFindDao.findAllById(listOf(inviterId, inviteeId))
            val inviter = memberMap.find(inviterId)
            val invitee = memberMap.find(inviteeId)
            friendRepository.save(Friend.create(inviter, invitee))
            return
        }

        friendValidateDao.validateAddFriend(friends)

        val friend =
            friends.find { it.inviter.id == inviterId && it.invitee.id == inviteeId && it.status.isReject() }!!
        friend.status = FriendStatus.PENDING
    }
}

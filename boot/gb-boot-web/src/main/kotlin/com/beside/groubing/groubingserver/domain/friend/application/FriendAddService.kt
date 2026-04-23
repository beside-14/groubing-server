package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberValidateDao
import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.dao.FriendValidateDao
import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.repository.MemberJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendAddService(
    private val blockedMemberValidateDao: BlockedMemberValidateDao,
    private val friendValidateDao: FriendValidateDao,
    private val friendFindDao: FriendFindDao,
    private val memberJpaRepository: MemberJpaRepository,
    private val friendRepository: FriendRepository
) {
    fun add(inviterId: Long, inviteeId: Long) {
        friendValidateDao.validateIsMe(inviterId, inviteeId)
        blockedMemberValidateDao.validateEachOther(inviterId, inviteeId)

        val friends = friendFindDao.findByFriends(inviterId, inviteeId)

        if (friends.isEmpty()) {
            val members = memberJpaRepository.findAllById(listOf(inviterId, inviteeId))
                .associateBy { it.id }
            val inviter = members[inviterId]
                ?: throw MemberInputException("존재하지 않는 유저 입니다.")
            val invitee = members[inviteeId]
                ?: throw MemberInputException("존재하지 않는 유저 입니다.")
            friendRepository.save(Friend.create(inviter, invitee))
            return
        }

        friendValidateDao.validateAddFriend(friends)

        val friend =
            friends.find { it.inviter.id == inviterId && it.invitee.id == inviteeId && it.status.isReject() }!!
        friend.status = FriendStatus.PENDING
    }
}

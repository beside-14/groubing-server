package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendValidateDao
import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendAddService(
    private val friendRepository: FriendRepository,
    private val friendValidateDao: FriendValidateDao,
    private val memberFindDao: MemberFindDao
) {
    fun add(inviterId: Long, inviteeId: Long) {
        // 차단 및 친구관계 검증
        friendValidateDao.validate(inviterId, inviteeId)

        // 친구 신청 요청
        val memberMap = memberFindDao.findAllById(listOf(inviterId, inviteeId))
        val inviter = memberMap.find(inviterId)
        val invitee = memberMap.find(inviteeId)
        val friend = Friend.create(inviter, invitee)
        friendRepository.save(friend)
    }
}
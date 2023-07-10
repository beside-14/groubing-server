package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.BlockedMemberExistDao
import com.beside.groubing.groubingserver.domain.member.dao.FriendExistDao
import com.beside.groubing.groubingserver.domain.member.domain.Friend
import com.beside.groubing.groubingserver.domain.member.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.member.exception.FriendInputException
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberFriendAddService(
    private val friendRepository: FriendRepository,
    private val friendExistDao: FriendExistDao,
    private val blockedMemberExistDao: BlockedMemberExistDao,
    private val memberFindDao: MemberFindDao
) {
    fun add(inviterId: Long, inviteeId: Long) {
        // 차단 여부 조회
        val isBlocked = blockedMemberExistDao.existByRequesterOrTargetMember(inviterId, inviteeId)
        if (isBlocked) throw FriendInputException("친구 신청이 불가능한 유저입니다.")

        // 관계 존재 여부 조회
        val hasFriendship = friendExistDao.existByInviterOrInvitee(inviterId, inviteeId)
        if (hasFriendship) throw FriendInputException("친구 신청이 불가능한 유저입니다.")

        // 친구 신청 요청
        val memberMap = memberFindDao.findAllById(listOf(inviterId, inviteeId))
        val inviter = memberMap.find(inviterId)
        val invitee = memberMap.find(inviteeId)
        val friend = Friend.create(inviter, invitee)
        friendRepository.save(friend)
    }
}

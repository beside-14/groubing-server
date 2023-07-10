package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.BlockedMemberExistDao
import com.beside.groubing.groubingserver.domain.member.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.member.domain.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.member.exception.BlockedMemberInputException
import com.beside.groubing.groubingserver.domain.member.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.member.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberBlockingService(
    private val blockedMemberRepository: BlockedMemberRepository,
    private val friendRepository: FriendRepository,
    private val blockedMemberExistDao: BlockedMemberExistDao,
    private val friendFindDao: FriendFindDao,
    private val memberFindDao: MemberFindDao
) {
    fun blocking(requesterId: Long, targetMemberId: Long) {
        // 차단 여부 확인
        val isBlocked = blockedMemberExistDao.existByRequesterOrTargetMember(requesterId, targetMemberId)
        if (isBlocked) throw BlockedMemberInputException("이미 차단 되었거나 차단이 불가능한 유저입니다.")

        // 친구 여부 확인 > 친구인 경우 친구 관계 삭제
        val friendships = friendFindDao.findByInviterOrInvitee(requesterId, targetMemberId)
        if (friendships.isNotEmpty()) friendRepository.deleteAll(friendships)

        // 차단 처리
        val memberMap = memberFindDao.findAllById(listOf(requesterId, targetMemberId))
        val requester = memberMap.find(requesterId)
        val targetMember = memberMap.find(targetMemberId)
        val blockedMember = BlockedMember.create(requester, targetMember)
        blockedMemberRepository.save(blockedMember)
    }
}

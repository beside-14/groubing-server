package com.beside.groubing.groubingserver.domain.blocked.application

import com.beside.groubing.groubingserver.domain.blocked.dao.BlockedMemberExistDao
import com.beside.groubing.groubingserver.domain.blocked.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.blocked.domain.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRepository
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlockMemberService(
    private val blockedMemberRepository: BlockedMemberRepository,
    private val friendRepository: FriendRepository,
    private val blockedMemberExistDao: BlockedMemberExistDao,
    private val friendFindDao: FriendFindDao,
    private val memberFindDao: MemberFindDao
) {
    fun block(requesterId: Long, targetMemberId: Long) {
        // 차단 여부 확인
        blockedMemberExistDao.existByRequesterOrTargetMember(requesterId, targetMemberId)

        // 친구 여부 확인 > 친구인 경우 친구 관계 삭제
        val friendships = friendFindDao.findByFriendships(requesterId, targetMemberId)
        if (friendships.isNotEmpty()) friendRepository.deleteAll(friendships)

        // 차단 처리
        val memberMap = memberFindDao.findAllById(listOf(requesterId, targetMemberId))
        val requester = memberMap.find(requesterId)
        val targetMember = memberMap.find(targetMemberId)
        val blockedMember = BlockedMember.create(requester, targetMember)
        blockedMemberRepository.save(blockedMember)
    }
}

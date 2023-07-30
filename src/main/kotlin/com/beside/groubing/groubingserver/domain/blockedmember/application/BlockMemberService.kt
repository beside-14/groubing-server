package com.beside.groubing.groubingserver.domain.blockedmember.application

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberValidateDao
import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.friend.dao.FriendDeleteDao
import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlockMemberService(
    private val blockedMemberRepository: BlockedMemberRepository,
    private val blockedMemberValidateDao: BlockedMemberValidateDao,
    private val friendDeleteDao: FriendDeleteDao,
    private val memberFindDao: MemberFindDao
) {
    fun block(requesterId: Long, targetMemberId: Long) {
        // 차단 여부 확인
        blockedMemberValidateDao.validateBlockMember(requesterId, targetMemberId)

        // 친구 여부 확인 > 친구인 경우 친구 관계 삭제
        friendDeleteDao.deleteAll(requesterId, targetMemberId)

        // 차단 처리
        val memberMap = memberFindDao.findAllById(listOf(requesterId, targetMemberId))
        val requester = memberMap.find(requesterId)
        val targetMember = memberMap.find(targetMemberId)
        val blockedMember = BlockedMember.create(requester, targetMember)
        blockedMemberRepository.save(blockedMember)
    }
}

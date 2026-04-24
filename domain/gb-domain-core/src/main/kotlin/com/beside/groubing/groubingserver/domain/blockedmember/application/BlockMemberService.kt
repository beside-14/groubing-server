package com.beside.groubing.groubingserver.domain.blockedmember.application

import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.blockedmember.exception.BlockedMemberInputException
import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendCommandRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlockMemberService(
    private val blockedMemberRepository: BlockedMemberRepository,
    private val friendCommandRepository: FriendCommandRepository
) {
    fun block(requesterId: Long, targetMemberId: Long) {
        if (blockedMemberRepository.existsByRequesterIdAndTargetMemberId(requesterId, targetMemberId)) {
            throw BlockedMemberInputException("이미 차단한 유저입니다.")
        }
        friendCommandRepository.deleteAllBetween(requesterId, targetMemberId)
        blockedMemberRepository.save(BlockedMember.create(requesterId, targetMemberId))
    }
}

package com.beside.groubing.domain.blockedmember.application

import com.beside.groubing.domain.blockedmember.domain.BlockMemberValidator
import com.beside.groubing.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.domain.friend.domain.port.FriendCommandRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlockMemberService(
    private val blockedMemberRepository: BlockedMemberRepository,
    private val friendCommandRepository: FriendCommandRepository,
    private val blockMemberValidator: BlockMemberValidator
) {
    fun block(requesterId: Long, targetMemberId: Long) {
        blockMemberValidator.validate(requesterId, targetMemberId)
        friendCommandRepository.deleteAllBetween(requesterId, targetMemberId)
        blockedMemberRepository.save(BlockedMember.create(requesterId, targetMemberId))
    }
}

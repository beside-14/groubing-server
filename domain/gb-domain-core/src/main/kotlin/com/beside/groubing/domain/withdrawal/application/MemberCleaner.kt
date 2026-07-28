package com.beside.groubing.domain.withdrawal.application

import com.beside.groubing.domain.auth.domain.port.SocialInfoRepository
import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.domain.common.file.application.FileStorage
import com.beside.groubing.domain.friend.domain.port.FriendCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.notification.domain.port.NotificationRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberCleaner(
    private val notificationRepository: NotificationRepository,
    private val friendCommandRepository: FriendCommandRepository,
    private val blockedMemberRepository: BlockedMemberRepository,
    private val socialInfoRepository: SocialInfoRepository,
    private val withdrawnMemberBingoCleaner: WithdrawnMemberBingoCleaner,
    private val memberCommandRepository: MemberCommandRepository
) {
    @Transactional
    fun cleanup(memberId: Long) {
        notificationRepository.deleteAllByMemberId(memberId)
        friendCommandRepository.deleteAllOf(memberId)
        blockedMemberRepository.deleteAllOf(memberId)
        socialInfoRepository.deleteAllByMemberId(memberId)
        val remainsInGroupBingo = withdrawnMemberBingoCleaner.cleanUpBoardsOf(memberId)
        removeMember(memberId, remainsInGroupBingo)
    }

    private fun removeMember(memberId: Long, remainsInGroupBingo: Boolean) {
        val previousProfile =
            if (remainsInGroupBingo) memberCommandRepository.tombstone(memberId)
            else memberCommandRepository.hardDelete(memberId)
        previousProfile?.let { FileStorage.delete(it) }
    }
}

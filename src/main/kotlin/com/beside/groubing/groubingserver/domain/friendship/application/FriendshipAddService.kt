package com.beside.groubing.groubingserver.domain.friendship.application

import com.beside.groubing.groubingserver.domain.friendship.domain.FriendshipRepository
import com.beside.groubing.groubingserver.domain.friendship.exception.FriendshipInputException
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendshipAddService(
    private val friendshipRepository: FriendshipRepository,
    private val memberRepository: MemberRepository
) {
    fun add(inviterId: Long, inviteeId: Long) {
        // 관계 존재 여부 조회
        val isInviteAsInviter = friendshipRepository.existsByInviterIdAndInviteeId(inviterId, inviteeId)
        val isInviteAsInvitee = friendshipRepository.existsByInviterIdAndInviteeId(inviteeId, inviterId)
        if (isInviteAsInviter || isInviteAsInvitee) throw FriendshipInputException("친구 신청을 처리할 수 없습니다.")

        // 친구 신청 요청
        val memberToId = memberRepository.findAllById(listOf(inviterId, inviteeId)).associateBy { member -> member.id }
        val findMember: (Long) -> Member = { id -> memberToId[id] ?: throw MemberInputException("존재하지 않는 유저 입니다.") }
        findMember(inviterId).addFriend(findMember(inviteeId))

        // TODO("친구 요청 푸시 발송 기능 구현하기")
    }
}

package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberDetailResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberFindFriendsService(
    private val memberFindDao: MemberFindDao
) {
    fun find(id: Long): List<MemberDetailResponse> {
        val member = memberFindDao.findExistingMemberById(id)
        return member.friends.members.map { friend ->
            MemberDetailResponse(
                friend.id,
                friend.email,
                friend.nickname,
                friend.profile?.url
            )
        }
    }

    fun findByNickname(id: Long, nickname: String): List<MemberDetailResponse> {
        val member = memberFindDao.findExistingMemberById(id)
        return member.friends.findByNickname(nickname).map { friend ->
            MemberDetailResponse(
                friend.id,
                friend.email,
                friend.nickname,
                friend.profile?.url
            )
        }
    }
}

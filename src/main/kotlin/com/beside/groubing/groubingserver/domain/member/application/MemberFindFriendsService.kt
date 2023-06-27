package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindFriendsDao
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberDetailResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberFindFriendsService(
    private val memberFindFriendsDao: MemberFindFriendsDao
) {
    fun findById(id: Long, pageable: Pageable): Page<MemberDetailResponse> {
        val friends = memberFindFriendsDao.findFriendsById(id, pageable)
        return friends.map(::MemberDetailResponse)
    }

    fun findByIdAndNickname(id: Long, pageable: Pageable, nickname: String): Page<MemberDetailResponse> {
        val friends = memberFindFriendsDao.findFriendsByIdAndNickname(id, pageable, nickname)
        return friends.map(::MemberDetailResponse)
    }
}

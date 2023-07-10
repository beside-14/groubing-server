package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.BlockedMemberFindDao
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberSummaryResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberBlockedListFindService(
    private val blockedMemberFindDao: BlockedMemberFindDao
) {
    fun findById(id: Long, pageable: Pageable): Page<MemberSummaryResponse> {
        val friends = blockedMemberFindDao.findAllById(id, pageable)
        return friends.map(::MemberSummaryResponse)
    }

    fun findByIdAndNickname(id: Long, pageable: Pageable, nickname: String): Page<MemberSummaryResponse> {
        val friends = blockedMemberFindDao.findAllByIdAndNickname(id, pageable, nickname)
        return friends.map(::MemberSummaryResponse)
    }
}

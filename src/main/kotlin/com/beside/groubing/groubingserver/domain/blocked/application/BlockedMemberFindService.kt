package com.beside.groubing.groubingserver.domain.blocked.application

import com.beside.groubing.groubingserver.domain.blocked.dao.BlockedMemberFindDao
import com.beside.groubing.groubingserver.domain.blocked.payload.response.BlockedMemberResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlockedMemberFindService(
    private val blockedMemberFindDao: BlockedMemberFindDao
) {
    fun findById(id: Long, pageable: Pageable): Page<BlockedMemberResponse> {
        val friends = blockedMemberFindDao.findAllById(id, pageable)
        return friends.map(::BlockedMemberResponse)
    }

    fun findByIdAndNickname(id: Long, pageable: Pageable, nickname: String): Page<BlockedMemberResponse> {
        val friends = blockedMemberFindDao.findAllByIdAndNickname(id, pageable, nickname)
        return friends.map(::BlockedMemberResponse)
    }
}

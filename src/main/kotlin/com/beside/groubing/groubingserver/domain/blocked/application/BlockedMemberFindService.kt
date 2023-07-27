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
    fun findById(id: Long): List<BlockedMemberResponse> {
        val friends = blockedMemberFindDao.findAllById(id)
        return friends.map(::BlockedMemberResponse)
    }
}

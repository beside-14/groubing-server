package com.beside.groubing.groubingserver.domain.blockedmember.application

import com.beside.groubing.groubingserver.domain.blockedmember.dao.BlockedMemberFindDao
import com.beside.groubing.groubingserver.domain.blockedmember.payload.response.BlockedMemberResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BlockedMemberFindService(
    private val blockedMemberFindDao: BlockedMemberFindDao
) {
    fun findById(id: Long): List<BlockedMemberResponse> {
        return blockedMemberFindDao.findAllRequestedBy(id).map(BlockedMemberResponse::create)
    }
}

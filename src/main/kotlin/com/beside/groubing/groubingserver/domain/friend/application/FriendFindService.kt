package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.payload.response.FriendResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendFindService(
    private val friendFindDao: FriendFindDao
) {
    fun findById(id: Long, pageable: Pageable): Page<FriendResponse> {
        val friends = friendFindDao.findAllById(id, pageable)
        return friends.map(::FriendResponse)
    }

    fun findByIdAndNickname(id: Long, pageable: Pageable, nickname: String): Page<FriendResponse> {
        val friends = friendFindDao.findAllByIdAndNickname(id, pageable, nickname)
        return friends.map(::FriendResponse)
    }
}

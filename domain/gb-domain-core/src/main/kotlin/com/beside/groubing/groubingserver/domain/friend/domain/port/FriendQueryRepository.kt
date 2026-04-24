package com.beside.groubing.groubingserver.domain.friend.domain.port

import com.beside.groubing.groubingserver.domain.friend.domain.Friend

interface FriendQueryRepository {
    fun findOne(id: Long): Friend

    fun findAllBetween(memberIdA: Long, memberIdB: Long): List<Friend>
}

package com.beside.groubing.groubingserver.domain.friend.domain.port

import com.beside.groubing.groubingserver.domain.friend.domain.Friend

interface FriendCommandRepository {
    fun save(friend: Friend): Friend

    fun update(friend: Friend): Friend

    fun deleteAllBetween(memberIdA: Long, memberIdB: Long)
}

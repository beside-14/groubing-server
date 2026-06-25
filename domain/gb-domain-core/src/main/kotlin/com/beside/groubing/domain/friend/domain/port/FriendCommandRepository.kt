package com.beside.groubing.domain.friend.domain.port

import com.beside.groubing.domain.friend.domain.Friend

interface FriendCommandRepository {
    fun save(friend: Friend): Friend

    fun update(friend: Friend): Friend

    fun deleteAllBetween(memberIdA: Long, memberIdB: Long)

    fun deleteAllOf(memberId: Long)
}

package com.beside.groubing.domain.friend.domain.port

import com.beside.groubing.domain.friend.domain.Friend
import com.beside.groubing.domain.friend.domain.FriendMember
import com.beside.groubing.domain.friend.domain.FriendStatus

interface FriendQueryRepository {
    fun findOne(id: Long): Friend

    fun findAllBetween(memberIdA: Long, memberIdB: Long): List<Friend>

    fun findAllAcceptedOf(memberId: Long): List<FriendMember>

    fun findAllReceivedBy(inviteeId: Long, statuses: Set<FriendStatus>): List<FriendMember>

    fun findAllSentBy(inviterId: Long, statuses: Set<FriendStatus>): List<FriendMember>
}

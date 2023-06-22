package com.beside.groubing.groubingserver.domain.friendship.domain

import org.springframework.data.jpa.repository.JpaRepository

interface FriendshipRepository : JpaRepository<Friendship, Long> {
    fun existsByInviterIdAndInviteeId(inviterId: Long, inviteeId: Long): Boolean
}

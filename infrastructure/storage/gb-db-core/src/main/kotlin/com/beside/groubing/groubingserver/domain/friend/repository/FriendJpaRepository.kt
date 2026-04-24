package com.beside.groubing.groubingserver.domain.friend.repository

import com.beside.groubing.groubingserver.domain.friend.entity.FriendEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FriendJpaRepository : JpaRepository<FriendEntity, Long> {
    @Query(
        """
        SELECT f FROM FriendEntity f
        WHERE (f.inviterId = :memberIdA AND f.inviteeId = :memberIdB)
           OR (f.inviterId = :memberIdB AND f.inviteeId = :memberIdA)
        """
    )
    fun findAllBetween(
        @Param("memberIdA") memberIdA: Long,
        @Param("memberIdB") memberIdB: Long
    ): List<FriendEntity>
}

package com.beside.groubing.groubingserver.domain.friend.entity

import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "FRIENDS")
class FriendEntity(
    @Column(name = "INVITER_ID")
    val inviterId: Long,

    @Column(name = "INVITEE_ID")
    val inviteeId: Long
) : BaseEntity() {
    @Id
    @Column(name = "FRIEND_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    @Enumerated(EnumType.STRING)
    var status: FriendStatus = FriendStatus.PENDING
        private set

    fun applyStatus(friend: Friend) {
        this.status = friend.status
    }

    fun toDomain(): Friend = Friend.of(
        id = id,
        inviterId = inviterId,
        inviteeId = inviteeId,
        status = status
    )

    companion object {
        fun from(friend: Friend): FriendEntity {
            val entity = FriendEntity(
                inviterId = friend.inviterId,
                inviteeId = friend.inviteeId
            )
            entity.status = friend.status
            return entity
        }
    }
}

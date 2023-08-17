package com.beside.groubing.groubingserver.domain.friend.domain

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "FRIENDS")
class Friend internal constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVITER_ID")
    val inviter: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVITEE_ID")
    val invitee: Member,
) : BaseEntity() {
    @Id
    @Column(name = "FRIEND_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var status: FriendStatus = FriendStatus.PENDING

    companion object {
        fun create(inviter: Member, invitee: Member): Friend {
            return Friend(inviter, invitee)
        }
    }

    fun isInvitee(memberId: Long): Boolean {
        return invitee.id == memberId
    }
}

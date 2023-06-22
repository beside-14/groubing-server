package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.domain.friendship.domain.Friendship
import jakarta.persistence.CascadeType
import jakarta.persistence.Embeddable
import jakarta.persistence.OneToMany

@Embeddable
class Friends private constructor(
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "inviter")
    private val friendshipsAsInviter: MutableList<Friendship>,
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "invitee")
    private val friendshipsAsInvitee: MutableList<Friendship>
) {
    val members: List<Member>
        get() {
            val friendsAsInviter =
                friendshipsAsInviter.filter { inviter -> inviter.status.isAccept() }.map { inviter -> inviter.invitee }
            val friendsAsInvitee =
                friendshipsAsInvitee.filter { invitee -> invitee.status.isAccept() }.map { invitee -> invitee.inviter }
            return friendsAsInviter + friendsAsInvitee
        }

    fun findByNickname(nickname: String): List<Member> {
        return members.filter { friend -> friend.nickname.contains(nickname) }
    }

    fun add(friendship: Friendship) {
        friendshipsAsInviter.add(friendship)
    }

    fun delete(friendId: Long) {
        friendshipsAsInviter.removeIf { friendship -> friendship.status.isAccept() && friendship.invitee.id == friendId }
        friendshipsAsInvitee.removeIf { friendship -> friendship.status.isAccept() && friendship.inviter.id == friendId }
    }

    companion object {
        fun create(): Friends {
            return Friends(mutableListOf(), mutableListOf())
        }
    }
}

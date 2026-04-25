package com.beside.groubing.domain.blockedmember.entity

import com.beside.groubing.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "BLOCKED_MEMBERS")
class BlockedMemberEntity(
    @Column(name = "REQUESTER_ID")
    val requesterId: Long,

    @Column(name = "TARGET_MEMBER_ID")
    val targetMemberId: Long
) : BaseEntity() {
    @Id
    @Column(name = "BLOCKED_MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    fun toDomain(): BlockedMember = BlockedMember.of(
        id = id,
        requesterId = requesterId,
        targetMemberId = targetMemberId
    )

    companion object {
        fun from(blockedMember: BlockedMember): BlockedMemberEntity = BlockedMemberEntity(
            requesterId = blockedMember.requesterId,
            targetMemberId = blockedMember.targetMemberId
        )
    }
}

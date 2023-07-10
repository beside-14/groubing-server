package com.beside.groubing.groubingserver.domain.member.domain

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
@Table(name = "BLOCKED_MEMBERS")
class BlockedMember internal constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTER_ID")
    val requester: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_MEMBER_ID")
    val targetMember: Member
) : BaseEntity() {
    @Id
    @Column(name = "BLOCKED_MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    companion object {
        fun create(requester: Member, targetMember: Member): BlockedMember {
            return BlockedMember(requester, targetMember)
        }
    }
}

package com.beside.groubing.domain.bingo.entity

import com.beside.groubing.domain.bingo.domain.BingoMember
import com.beside.groubing.domain.bingo.domain.BingoMemberType
import com.beside.groubing.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "BINGO_MEMBERS")
class BingoMemberEntity(
    val memberId: Long,

    bingoMemberType: BingoMemberType,

    active: Boolean = true
) : BaseEntity() {
    @Id
    @Column(name = "BINGO_MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    @Enumerated(EnumType.STRING)
    var bingoMemberType: BingoMemberType = bingoMemberType
        private set

    var active: Boolean = active
        private set

    fun deactivate() {
        this.active = false
    }

    fun promoteToLeader() {
        this.bingoMemberType = BingoMemberType.LEADER
    }

    fun demoteToParticipant() {
        this.bingoMemberType = BingoMemberType.PARTICIPANT
    }

    fun applyChanges(domain: BingoMember) {
        this.active = domain.active
    }

    fun toDomain(): BingoMember = BingoMember.of(
        id = id,
        memberId = memberId,
        bingoMemberType = bingoMemberType,
        active = active
    )

    companion object {
        fun from(domain: BingoMember): BingoMemberEntity {
            return BingoMemberEntity(
                memberId = domain.memberId,
                bingoMemberType = domain.bingoMemberType,
                active = domain.active
            )
        }
    }
}

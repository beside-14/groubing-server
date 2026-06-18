package com.beside.groubing.domain.bingo.entity

import com.beside.groubing.domain.bingo.domain.BingoCompleteMember
import com.beside.groubing.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "BINGO_COMPLETE_MEMBERS")
class BingoCompleteMemberEntity(
    val memberId: Long,
    active: Boolean = true
) : BaseEntity() {
    @Id
    @Column(name = "BINGO_COMPLETE_MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var active: Boolean = active
        private set

    fun deactivate() {
        this.active = false
    }

    fun applyChanges(domain: BingoCompleteMember) {
        this.active = domain.active
    }

    fun toDomain(): BingoCompleteMember = BingoCompleteMember.of(
        id = id,
        memberId = memberId,
        active = active
    )

    companion object {
        fun from(domain: BingoCompleteMember): BingoCompleteMemberEntity {
            return BingoCompleteMemberEntity(memberId = domain.memberId, active = domain.active)
        }
    }
}

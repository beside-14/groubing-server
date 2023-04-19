package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "BINGO_MEMBERS")
class BingoMember private constructor(
    @Id
    @Column(name = "BINGO_MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val memberId: Long,

    val bingoMemberType: BingoMemberType
) : BaseEntity() {

    companion object {
        fun createBingoMember(memberId: Long, bingoMemberType: BingoMemberType): BingoMember =
            BingoMember(memberId = memberId, bingoMemberType = bingoMemberType)
    }
}

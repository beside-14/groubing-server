package com.beside.groubing.groubingserver.domain.bingo.domain

class BingoMember private constructor(
    val id: Long,
    val memberId: Long,
    val bingoMemberType: BingoMemberType,
    active: Boolean
) {
    var active: Boolean = active
        private set

    fun isLeader(): Boolean = bingoMemberType.isLeader()

    fun inactive() {
        this.active = false
    }

    companion object {
        fun create(memberId: Long, bingoMemberType: BingoMemberType = BingoMemberType.PARTICIPANT): BingoMember {
            return BingoMember(id = 0L, memberId = memberId, bingoMemberType = bingoMemberType, active = true)
        }

        fun of(id: Long, memberId: Long, bingoMemberType: BingoMemberType, active: Boolean): BingoMember {
            return BingoMember(id = id, memberId = memberId, bingoMemberType = bingoMemberType, active = active)
        }
    }
}

package com.beside.groubing.domain.bingo.domain

class BingoCompleteMember private constructor(
    val id: Long,
    val memberId: Long,
    active: Boolean
) {
    var active: Boolean = active
        private set

    fun inactive() {
        this.active = false
    }

    companion object {
        fun create(memberId: Long): BingoCompleteMember {
            return BingoCompleteMember(id = 0L, memberId = memberId, active = true)
        }

        fun of(id: Long, memberId: Long, active: Boolean): BingoCompleteMember {
            return BingoCompleteMember(id = id, memberId = memberId, active = active)
        }
    }
}

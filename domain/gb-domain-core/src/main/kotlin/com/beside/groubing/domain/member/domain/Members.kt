package com.beside.groubing.domain.member.domain

class Members private constructor(val data: List<Member>) : Iterable<Member> {

    override fun iterator(): Iterator<Member> = data.iterator()

    fun anonymizeWithdrawn(): Members = Members(data.map { it.anonymizeIfWithdrawn() })

    companion object {
        fun of(members: List<Member>): Members = Members(members)
    }
}

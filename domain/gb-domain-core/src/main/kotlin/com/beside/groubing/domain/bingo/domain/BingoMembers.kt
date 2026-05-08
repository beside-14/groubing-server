package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.exception.BingoIllegalStateException
import com.beside.groubing.domain.bingo.exception.BingoInputException

class BingoMembers private constructor(val data: MutableList<BingoMember>) : Iterable<BingoMember> {

    override fun iterator(): Iterator<BingoMember> = data.iterator()

    operator fun get(index: Int): BingoMember = data[index]

    fun memberIds(): List<Long> = data.map { it.memberId }

    fun otherMemberIdsOf(memberId: Long): List<Long> =
        data.filter { it.memberId != memberId }.map { it.memberId }

    fun otherActiveMemberIdsOf(memberId: Long): List<Long> =
        data.filter { it.memberId != memberId && it.active }.map { it.memberId }

    fun isLeaderOf(memberId: Long): Boolean =
        data.any { it.memberId == memberId && it.isLeader() }

    fun findOf(memberId: Long, bingoBoardId: Long): BingoMember =
        data.find { it.memberId == memberId }
            ?: throw BingoInputException(
                "해당 그룹 빙고에 포함되지 않은 빙고 memberId입니다. bingoBoardId: $bingoBoardId, memberId: $memberId"
            )

    fun validateLeaderOf(memberId: Long, bingoBoardId: Long) {
        val bingoMember = findOf(memberId, bingoBoardId)
        if (!bingoMember.isLeader()) {
            throw BingoIllegalStateException("해당 빙고를 수정할 권한이 없습니다.")
        }
    }

    fun addNewMembers(memberIds: List<Long>) {
        data.addAll(memberIds.map { BingoMember.create(it) })
    }

    fun inactivateOf(memberId: Long, bingoBoardId: Long) {
        val bingoMember = data.find { it.memberId == memberId }
            ?: throw BingoIllegalStateException(
                "해당 빙고보드에 포함되지 않는 회원입니다. bingoBoardId: $bingoBoardId, memberId: $memberId"
            )
        bingoMember.inactive()
    }

    companion object {
        fun of(members: MutableList<BingoMember>): BingoMembers = BingoMembers(members)

        fun ofLeader(leaderId: Long): BingoMembers =
            BingoMembers(mutableListOf(BingoMember.create(leaderId, BingoMemberType.LEADER)))
    }
}

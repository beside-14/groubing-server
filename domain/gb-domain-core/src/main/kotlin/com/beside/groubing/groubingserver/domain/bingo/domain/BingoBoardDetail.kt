package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.member.domain.Member

data class BingoBoardDetail(
    val bingoBoard: BingoBoard,
    val viewer: Member,
    val otherMembers: List<Member>
)

package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.member.domain.Member

data class BingoBoardDetail(
    val bingoBoard: BingoBoard,
    val viewer: Member,
    val otherMembers: List<Member>
)

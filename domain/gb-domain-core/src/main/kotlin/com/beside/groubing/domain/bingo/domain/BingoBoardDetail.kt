package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.Members

data class BingoBoardDetail(
    val bingoBoard: BingoBoard,
    val viewer: Member,
    val otherMembers: Members
)

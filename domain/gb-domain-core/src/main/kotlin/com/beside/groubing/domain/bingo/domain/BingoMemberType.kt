package com.beside.groubing.domain.bingo.domain

enum class BingoMemberType(val description: String) {
    LEADER("빙고 리더"),
    PARTICIPANT("참여자");

    fun isLeader(): Boolean {
        return this == LEADER
    }
}

package com.beside.groubing.groubingserver.domain.member.domain

enum class FriendshipStatus(val description: String) {
    PENDING("응답대기"),
    ACCEPT("수락"),
    REJECT("거절");

    fun isPending(): Boolean {
        return this == PENDING
    }

    fun isAccept(): Boolean {
        return this == ACCEPT
    }
}

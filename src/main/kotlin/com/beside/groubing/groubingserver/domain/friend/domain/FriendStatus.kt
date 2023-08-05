package com.beside.groubing.groubingserver.domain.friend.domain

enum class FriendStatus(val description: String) {
    PENDING("응답대기"),
    REJECT("거절"),
    ACCEPT("수락");

    fun isPending(): Boolean {
        return this == PENDING
    }

    fun isAccept(): Boolean {
        return this == ACCEPT
    }

    fun isReject(): Boolean {
        return this == REJECT
    }
}

package com.beside.groubing.groubingserver.domain.friend.domain

enum class FriendStatus(val description: String) {
    PENDING("응답대기"),
    ACCEPT("수락");

    fun isAccept(): Boolean {
        return this == ACCEPT
    }
}

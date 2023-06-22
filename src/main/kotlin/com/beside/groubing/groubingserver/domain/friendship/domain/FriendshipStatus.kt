package com.beside.groubing.groubingserver.domain.friendship.domain

enum class FriendshipStatus(val description: String) {
    PENDING("응답대기"),
    ACCEPT("수락"),
    REJECT("거절");

    companion object {
        fun acceptOrReject(isAccept: Boolean): FriendshipStatus {
            return if (isAccept) ACCEPT else REJECT
        }
    }

    fun isPending(): Boolean {
        return this == PENDING
    }

    fun isAccept(): Boolean {
        return this == ACCEPT
    }
}

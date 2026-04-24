package com.beside.groubing.groubingserver.domain.friend.domain

import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException

class FriendRelations(val data: List<Friend>) {
    fun findRependable(inviterId: Long, inviteeId: Long): Friend {
        if (data.any { !it.status.isReject() }) {
            throw FriendInputException("이미 등록된 친구이거나 친구 요청 대기 상태입니다.")
        }
        return data.find { it.inviterId == inviterId && it.inviteeId == inviteeId && it.status.isReject() }
            ?: throw FriendInputException("다시 요청할 수 있는 친구 기록이 없습니다.")
    }
}

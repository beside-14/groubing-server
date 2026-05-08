package com.beside.groubing.vocabulary

import com.beside.groubing.docs.ENUM
import com.beside.groubing.docs.NUMBER
import com.beside.groubing.docs.requestParam
import com.beside.groubing.docs.responseType
import com.beside.groubing.domain.friend.domain.FriendStatus

fun friendId(fieldName: String = "id", description: String = "친구 요청 ID") =
    fieldName responseType NUMBER means description example "1"

fun friendStatus(fieldName: String = "status") =
    fieldName responseType ENUM(FriendStatus::class) means
        "친구 요청 처리 상태" example
        "`PENDING` : 친구 요청 / `ACCEPT` : 수락 / `REJECT` : 거절"

fun friendIdPath(fieldName: String = "id") =
    fieldName requestParam "친구 요청 ID"

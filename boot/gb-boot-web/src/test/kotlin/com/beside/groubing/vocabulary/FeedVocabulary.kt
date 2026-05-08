package com.beside.groubing.vocabulary

import com.beside.groubing.docs.BOOLEAN
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.responseType

fun feedItemTitle(fieldName: String = "[].feedItems[].title") =
    fieldName responseType STRING means "빙고 Item Title" example "토익 만점 받기"

fun isFriendRequestReceived(fieldName: String = "[].isFriendRequestReceived") =
    fieldName responseType BOOLEAN means "나에게 친구요청을 보낸 사람 여부" example "true"

fun isFriendRequestSend(fieldName: String = "[].isFriendRequestSend") =
    fieldName responseType BOOLEAN means "내가 친구요청을 보낸 사람 여부" example "false"

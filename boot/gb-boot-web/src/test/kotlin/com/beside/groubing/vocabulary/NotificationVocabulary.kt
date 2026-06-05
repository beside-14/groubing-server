package com.beside.groubing.vocabulary

import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.responseType

fun notificationBingoBoardId(fieldName: String = "bingoBoardId") =
    fieldName responseType STRING means "빙고보드 ID (obfuscated)" example "MEbN4aLpqRzK"

fun notificationMessage(fieldName: String = "message") =
    fieldName responseType STRING means "알림 메세지" example "awaji님이 이직 준비하기 빙고의 목표 빙고 수를 달성했어요!"

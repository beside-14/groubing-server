package com.beside.groubing.vocabulary

import com.beside.groubing.docs.BOOLEAN
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.responseType

fun accessToken(fieldName: String = "token") =
    fieldName responseType STRING means "유저 JWT 토큰"

fun hasNickname(fieldName: String = "hasNickname") =
    fieldName responseType BOOLEAN means "초기 닉네임 설정여부" example "true"

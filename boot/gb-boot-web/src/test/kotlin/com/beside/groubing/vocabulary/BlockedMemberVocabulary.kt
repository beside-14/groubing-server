package com.beside.groubing.vocabulary

import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.requestParam
import com.beside.groubing.docs.responseType

fun blockedMemberId(fieldName: String = "id", description: String = "차단 ID (obfuscated)") =
    fieldName responseType STRING means description example "MEbN4aLpqRzK"

fun blockedMemberIdPath(fieldName: String = "blockedMemberId") =
    fieldName requestParam "차단 ID (obfuscated)"

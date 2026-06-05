package com.beside.groubing.vocabulary

import com.beside.groubing.docs.BOOLEAN
import com.beside.groubing.docs.ENUM
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.requestParam
import com.beside.groubing.docs.responseType
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType

// --- Path Variable ---

fun memberIdPath(fieldName: String = "id") =
    fieldName requestParam "유저 ID (obfuscated)"

// --- 회원 식별자 (obfuscated 문자열) ---

fun memberId(fieldName: String = "memberId", description: String = "회원 ID (obfuscated)") =
    fieldName responseType STRING means description example "MEbN4aLpqRzK"

fun targetMemberId(fieldName: String = "targetMemberId", description: String = "대상 회원 ID (obfuscated)") =
    fieldName responseType STRING means description example "MEbN4aLpqRzK"

// --- 회원 프로필 ---

fun email(fieldName: String = "email") =
    fieldName responseType STRING means "이메일" example "test@groubing.com"

fun nickname(fieldName: String = "nickname") =
    fieldName responseType STRING means "닉네임" example "그루빙멤버"

fun profileUrl(fieldName: String = "profileUrl") =
    fieldName responseType STRING means "프로필 이미지 URL" isOptional true

fun fcmToken(fieldName: String = "fcmToken") =
    fieldName responseType STRING means "FCM 푸시 토큰" isOptional true

fun notificationReceive(fieldName: String = "notificationReceive") =
    fieldName responseType BOOLEAN means "푸시 알림 수신 여부" example "true"

fun memberRole(fieldName: String = "role") =
    fieldName responseType ENUM(MemberRole::class) means "회원 권한" example "MEMBER"

fun memberType(fieldName: String = "memberType") =
    fieldName responseType ENUM(MemberType::class) means "회원 가입 유형" example "CLASSIC"

package com.beside.groubing.domain.member.domain

import com.beside.groubing.domain.member.exception.MemberInputException
import java.time.LocalDateTime

data class Member(
    val id: Long,
    val loginId: String?,
    val password: String,
    val nickname: String,
    val role: MemberRole,
    val memberType: MemberType,
    val fcmToken: String?,
    val notificationReceive: Boolean,
    val active: Boolean,
    val deletedAt: LocalDateTime?,
    val profileUrl: String?
) {
    fun withFcmToken(fcmToken: String?): Member = copy(fcmToken = fcmToken)

    fun withNickname(nickname: String): Member = copy(nickname = nickname)

    fun withPassword(encodedPassword: String): Member = copy(password = encodedPassword)

    fun withNotificationReceive(receive: Boolean): Member = copy(notificationReceive = receive)

    fun validateWithdrawable() {
        if (!active) {
            throw MemberInputException("이미 탈퇴한 회원입니다.")
        }
    }

    fun hasNickname(): Boolean = nickname.isNotBlank()

    fun isWithdrawn(): Boolean = deletedAt != null

    fun anonymizeIfWithdrawn(): Member =
        if (isWithdrawn()) copy(nickname = WITHDRAWN_NICKNAME, profileUrl = null) else this

    companion object {
        const val WITHDRAWN_NICKNAME = "탈퇴한 회원"
    }
}

package com.beside.groubing.domain.member.domain

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
    val profileUrl: String?
) {
    fun withFcmToken(fcmToken: String?): Member = copy(fcmToken = fcmToken)

    fun withNickname(nickname: String): Member = copy(nickname = nickname)

    fun withPassword(encodedPassword: String): Member = copy(password = encodedPassword)

    fun withNotificationReceive(receive: Boolean): Member = copy(notificationReceive = receive)

    fun withdrawn(): Member {
        check(active) { "이미 탈퇴한 회원입니다." }
        return copy(active = false)
    }

    fun hasNickname(): Boolean = nickname.isNotBlank()
}

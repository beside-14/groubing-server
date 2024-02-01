package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo
import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.security.crypto.password.PasswordEncoder

@Entity
@Table(name = "MEMBERS")
class Member internal constructor(
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val email: String?,

    password: String,

    nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    val role: MemberRole,

    @Enumerated(EnumType.STRING)
    val memberType: MemberType

) : BaseEntity() {
    var password: String = password
        private set

    var nickname: String = nickname
        private set

    var fcmToken: String? = null
        private set

    var notificationReceive: Boolean = true
        private set

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "PROFILE_ID")
    var profile: FileInfo? = null

    fun matches(password: String, passwordEncoder: PasswordEncoder) {
        if (!passwordEncoder.matches(password, this.password)) {
            throw MemberInputException("비밀번호가 일치하지 않습니다.")
        }
    }

    fun editFcmToken(fcmToken: String?) {
        this.fcmToken = fcmToken
    }

    fun deleteFcmToken() {
        this.fcmToken = null
    }

    fun editNickname(nickname: String) {
        this.nickname = nickname
    }

    fun editPassword(encodedPassword: String) {
        this.password = encodedPassword
    }

    fun maskEmail(): String {
        if (email == null) {
            throw IllegalStateException("email이 존재하지 않는 계정입니다.")
        }
        val endIndex = email.indexOfFirst { it == '@' }
        val startIndex = endIndex / 2
        val replacement = (startIndex until endIndex).map { '*' }.joinToString("")
        return StringBuilder(email).replace(startIndex, endIndex, replacement).toString()
    }

    fun editProfile(profile: FileInfo) {
        this.profile = profile
    }

    fun deleteProfile() {
        this.profile = null
    }

    companion object {
        fun create(email: String, password: String, nickname: String, role: MemberRole): Member {
            return Member(email = email, password = password, nickname = nickname, role = role, memberType = MemberType.CLASSIC)
        }

        fun createSocialMember(email: String?): Member {
            return Member(email = email, password = "", nickname = "",
            role = MemberRole.MEMBER, memberType = MemberType.SOCIAL)
        }
    }
}

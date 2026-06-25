package com.beside.groubing.domain.member.entity

import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.domain.NewMember
import com.beside.groubing.domain.common.file.entity.FileInfoEntity
import com.beside.groubing.global.domain.jpa.BaseEntity
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
import java.time.LocalDateTime

@Entity
@Table(name = "MEMBERS")
class MemberEntity(
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    loginId: String?,

    password: String,

    nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    val role: MemberRole,

    @Enumerated(EnumType.STRING)
    val memberType: MemberType
) : BaseEntity() {
    @Column(name = "LOGIN_ID", unique = true)
    var loginId: String? = loginId
        private set

    var password: String = password
        private set

    @Column(name = "NICKNAME", unique = true)
    var nickname: String = nickname
        private set

    var fcmToken: String? = null
        private set

    var notificationReceive: Boolean = true
        private set

    var active: Boolean = true
        private set

    @Column(name = "DELETED_AT")
    var deletedAt: LocalDateTime? = null
        private set

    @Column(name = "CLEANED_AT")
    var cleanedAt: LocalDateTime? = null
        private set

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "PROFILE_ID")
    var profile: FileInfoEntity? = null

    fun applyChanges(member: Member) {
        this.password = member.password
        this.nickname = member.nickname
        this.fcmToken = member.fcmToken
        this.notificationReceive = member.notificationReceive
        this.active = member.active
    }

    fun withdraw(now: LocalDateTime) {
        this.active = false
        this.deletedAt = now
    }

    fun tombstone(now: LocalDateTime) {
        this.loginId = null
        this.password = ""
        this.nickname = "탈퇴회원_$id"
        this.fcmToken = null
        this.notificationReceive = false
        this.profile = null
        this.cleanedAt = now
    }

    fun editProfile(profile: FileInfoEntity) {
        this.profile = profile
    }

    fun deleteProfile() {
        this.profile = null
    }

    fun toDomain(): Member = Member(
        id = id,
        loginId = loginId,
        password = password,
        nickname = nickname,
        role = role,
        memberType = memberType,
        fcmToken = fcmToken,
        notificationReceive = notificationReceive,
        active = active,
        deletedAt = deletedAt,
        profileUrl = profile?.toDomain()?.url
    )

    companion object {
        fun from(newMember: NewMember): MemberEntity = MemberEntity(
            loginId = newMember.loginId,
            password = newMember.password,
            nickname = newMember.nickname,
            role = newMember.role,
            memberType = newMember.memberType
        )
    }
}

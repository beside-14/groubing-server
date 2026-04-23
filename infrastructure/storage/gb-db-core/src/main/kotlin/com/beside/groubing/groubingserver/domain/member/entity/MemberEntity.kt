package com.beside.groubing.groubingserver.domain.member.entity

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import com.beside.groubing.groubingserver.domain.member.domain.MemberType
import com.beside.groubing.groubingserver.domain.member.domain.NewMember
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
import org.hibernate.annotations.Where

@Entity
@Where(clause = "active = true")
@Table(name = "MEMBERS")
class MemberEntity(
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "EMAIL", unique = true)
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

    @Column(name = "NICKNAME", unique = true)
    var nickname: String = nickname
        private set

    var fcmToken: String? = null
        private set

    var notificationReceive: Boolean = true
        private set

    var active: Boolean = true
        private set

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "PROFILE_ID")
    var profile: FileInfo? = null

    fun applyChanges(member: Member) {
        this.password = member.password
        this.nickname = member.nickname
        this.fcmToken = member.fcmToken
        this.notificationReceive = member.notificationReceive
        this.active = member.active
    }

    fun editProfile(profile: FileInfo) {
        this.profile = profile
    }

    fun deleteProfile() {
        this.profile = null
    }

    fun toDomain(): Member = Member(
        id = id,
        email = email,
        password = password,
        nickname = nickname,
        role = role,
        memberType = memberType,
        fcmToken = fcmToken,
        notificationReceive = notificationReceive,
        active = active,
        profileUrl = profile?.url
    )

    companion object {
        fun from(newMember: NewMember): MemberEntity = MemberEntity(
            email = newMember.email,
            password = newMember.password,
            nickname = newMember.nickname,
            role = newMember.role,
            memberType = newMember.memberType
        )
    }
}

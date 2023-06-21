package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo
import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
@Table(name = "MEMBERS")
class Member internal constructor(
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val email: String,
    password: String,
    nickname: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    val role: MemberRole
) : BaseEntity() {
    var password: String = password
        private set

    var nickname: String = nickname
        private set

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "PROFILE_ID")
    var profile: FileInfo? = null

    @Embedded
    val friends: Friends = Friends.create()

    fun matches(password: String, passwordEncoder: BCryptPasswordEncoder) {
        if (!passwordEncoder.matches(password, this.password)) {
            throw MemberInputException("비밀번호가 일치하지 않습니다.")
        }
    }

    fun editNickname(nickname: String) {
        this.nickname = nickname
    }

    fun editPassword(encodedPassword: String) {
        this.password = encodedPassword
    }

    fun maskEmail(): String {
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

    fun addFriend(invitee: Member): Friendship {
        val friendship = Friendship.create(this, invitee)
        friends.add(friendship)
        return friendship
    }

    fun deleteFriend(friend: Member) {
        friends.delete(friend.id)
    }

    companion object {
        fun create(email: String, password: String, nickname: String, role: MemberRole): Member {
            return Member(email = email, password = password, nickname = nickname, role = role)
        }
    }
}

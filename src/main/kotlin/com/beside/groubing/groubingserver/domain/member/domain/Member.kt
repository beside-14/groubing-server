package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
@Table(name = "MEMBERS")
class Member private constructor(
    val email: String,
    password: String,
    nickname: String
) : BaseEntity() {
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var password: String = password
        private set

    var nickname: String = nickname
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    val role: MemberRole = MemberRole.MEMBER

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

    companion object {
        fun create(email: String, password: String, nickname: String): Member {
            return Member(email, password, nickname)
        }
    }
}

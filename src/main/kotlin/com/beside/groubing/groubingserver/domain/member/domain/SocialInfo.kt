package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "SOCIAL_INFOS")
class SocialInfo internal constructor(
    val email: String,

    @Enumerated(EnumType.STRING)
    val socialType: SocialType,

    val memberId: Long
): BaseEntity() {
    @Id
    @Column(name = "SOCIAL_INFO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}

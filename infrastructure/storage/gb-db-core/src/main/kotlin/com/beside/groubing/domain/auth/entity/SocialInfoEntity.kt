package com.beside.groubing.domain.auth.entity

import com.beside.groubing.domain.auth.domain.SocialInfo
import com.beside.groubing.domain.auth.domain.SocialType
import com.beside.groubing.global.domain.jpa.BaseEntity
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
class SocialInfoEntity(
    val socialId: String,

    val email: String?,

    @Enumerated(EnumType.STRING)
    val socialType: SocialType,

    val memberId: Long
) : BaseEntity() {
    @Id
    @Column(name = "SOCIAL_INFO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    fun toDomain(): SocialInfo = SocialInfo.of(
        id = id,
        socialId = socialId,
        email = email,
        socialType = socialType,
        memberId = memberId
    )

    companion object {
        fun from(socialInfo: SocialInfo): SocialInfoEntity = SocialInfoEntity(
            socialId = socialInfo.socialId,
            email = socialInfo.email,
            socialType = socialInfo.socialType,
            memberId = socialInfo.memberId
        )
    }
}

package com.beside.groubing.groubingserver.domain.auth.domain

class SocialInfo private constructor(
    val id: Long,
    val socialId: String,
    val email: String?,
    val socialType: SocialType,
    val memberId: Long
) {
    companion object {
        fun create(
            socialId: String,
            email: String?,
            socialType: SocialType,
            memberId: Long
        ): SocialInfo = SocialInfo(
            id = 0L,
            socialId = socialId,
            email = email,
            socialType = socialType,
            memberId = memberId
        )

        fun of(
            id: Long,
            socialId: String,
            email: String?,
            socialType: SocialType,
            memberId: Long
        ): SocialInfo = SocialInfo(
            id = id,
            socialId = socialId,
            email = email,
            socialType = socialType,
            memberId = memberId
        )
    }
}

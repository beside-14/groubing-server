package com.beside.groubing.domain.auth.domain

class SocialInfo private constructor(
    val id: Long,
    val socialId: String,
    val socialType: SocialType,
    val memberId: Long
) {
    companion object {
        fun create(
            socialId: String,
            socialType: SocialType,
            memberId: Long
        ): SocialInfo = SocialInfo(
            id = 0L,
            socialId = socialId,
            socialType = socialType,
            memberId = memberId
        )

        fun of(
            id: Long,
            socialId: String,
            socialType: SocialType,
            memberId: Long
        ): SocialInfo = SocialInfo(
            id = id,
            socialId = socialId,
            socialType = socialType,
            memberId = memberId
        )
    }
}

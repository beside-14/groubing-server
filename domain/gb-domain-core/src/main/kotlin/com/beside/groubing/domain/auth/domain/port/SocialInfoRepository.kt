package com.beside.groubing.domain.auth.domain.port

import com.beside.groubing.domain.auth.domain.SocialInfo
import com.beside.groubing.domain.auth.domain.SocialType

interface SocialInfoRepository {
    fun save(socialInfo: SocialInfo): SocialInfo

    fun findBySocialIdAndSocialTypeOrNull(socialId: String, socialType: SocialType): SocialInfo?

    fun deleteAllByMemberId(memberId: Long)
}

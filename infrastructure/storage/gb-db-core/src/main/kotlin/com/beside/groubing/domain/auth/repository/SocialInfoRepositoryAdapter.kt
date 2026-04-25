package com.beside.groubing.domain.auth.repository

import com.beside.groubing.domain.auth.domain.SocialInfo
import com.beside.groubing.domain.auth.domain.SocialType
import com.beside.groubing.domain.auth.domain.port.SocialInfoRepository
import com.beside.groubing.domain.auth.entity.SocialInfoEntity
import org.springframework.stereotype.Repository

@Repository
class SocialInfoRepositoryAdapter(
    private val socialInfoJpaRepository: SocialInfoJpaRepository
) : SocialInfoRepository {
    override fun save(socialInfo: SocialInfo): SocialInfo {
        return socialInfoJpaRepository.save(SocialInfoEntity.from(socialInfo)).toDomain()
    }

    override fun findBySocialIdAndSocialTypeOrNull(socialId: String, socialType: SocialType): SocialInfo? {
        return socialInfoJpaRepository.findBySocialIdAndSocialType(socialId, socialType)?.toDomain()
    }
}

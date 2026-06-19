package com.beside.groubing.domain.auth.repository

import com.beside.groubing.domain.auth.domain.SocialType
import com.beside.groubing.domain.auth.entity.SocialInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SocialInfoJpaRepository : JpaRepository<SocialInfoEntity, Long> {
    fun findBySocialIdAndSocialType(socialId: String, socialType: SocialType): SocialInfoEntity?

    fun deleteByMemberId(memberId: Long)
}

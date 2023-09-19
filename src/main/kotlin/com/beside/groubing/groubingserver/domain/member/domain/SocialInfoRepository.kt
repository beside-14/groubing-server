package com.beside.groubing.groubingserver.domain.member.domain

import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository

interface SocialInfoRepository : JpaRepository<SocialInfo, Long> {

    fun findBySocialIdAndSocialType(socialId: String, socialType: SocialType): Optional<SocialInfo>

}

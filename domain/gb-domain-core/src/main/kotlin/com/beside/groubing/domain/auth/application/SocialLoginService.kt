package com.beside.groubing.domain.auth.application

import com.beside.groubing.domain.auth.application.command.SocialLoginCommand
import com.beside.groubing.domain.auth.domain.AuthenticatedMember
import com.beside.groubing.domain.auth.domain.SocialInfo
import com.beside.groubing.domain.auth.domain.port.SocialInfoRepository
import com.beside.groubing.domain.auth.domain.port.TokenManager
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.domain.NewMember
import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SocialLoginService(
    private val socialInfoRepository: SocialInfoRepository,
    private val memberCommandRepository: MemberCommandRepository,
    private val memberQueryRepository: MemberQueryRepository,
    private val tokenManager: TokenManager
) {
    fun login(socialLoginCommand: SocialLoginCommand): AuthenticatedMember {
        val socialInfo = findOrCreateSocialInfo(socialLoginCommand)
        val member = memberQueryRepository.findById(socialInfo.memberId)
        val updated = memberCommandRepository.update(member.withFcmToken(socialLoginCommand.fcmToken))
        return AuthenticatedMember(updated, tokenManager.generateAccessToken(updated.id, updated.role.name))
    }

    private fun findOrCreateSocialInfo(socialLoginCommand: SocialLoginCommand): SocialInfo {
        return socialInfoRepository.findBySocialIdAndSocialTypeOrNull(
            socialLoginCommand.id,
            socialLoginCommand.socialType
        ) ?: run {
            val member = memberCommandRepository.save(
                NewMember(
                    email = socialLoginCommand.email,
                    password = "",
                    nickname = "",
                    role = MemberRole.MEMBER,
                    memberType = MemberType.SOCIAL
                )
            )
            socialInfoRepository.save(
                SocialInfo.create(
                    socialId = socialLoginCommand.id,
                    email = socialLoginCommand.email,
                    socialType = socialLoginCommand.socialType,
                    memberId = member.id
                )
            )
        }
    }
}

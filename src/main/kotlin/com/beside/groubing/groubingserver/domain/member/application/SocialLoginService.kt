package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.dao.MemberFindDao
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.domain.SocialInfo
import com.beside.groubing.groubingserver.domain.member.domain.SocialInfoRepository
import com.beside.groubing.groubingserver.domain.member.payload.command.SocialLoginCommand
import com.beside.groubing.groubingserver.domain.member.payload.response.SocialMemberResponse
import com.beside.groubing.groubingserver.global.domain.security.JwtProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SocialLoginService(
    private val socialInfoRepository: SocialInfoRepository,

    private val memberRepository: MemberRepository,

    private val memberFindDao: MemberFindDao
) {
    fun login(socialLoginCommand: SocialLoginCommand): SocialMemberResponse {
        val socialInfo = findOrCreateSocialInfo(socialLoginCommand)
        val member = memberFindDao.findExistingMemberById(socialInfo.memberId)
        return createSocialMemberResponse(member = member, hasNickname = member.nickname.isNotBlank())
    }

    private fun findOrCreateSocialInfo(socialLoginCommand: SocialLoginCommand): SocialInfo {
        val socialInfo = socialInfoRepository.findByEmailAndSocialType(
            socialLoginCommand.email,
            socialLoginCommand.socialType
        ).orElseGet {
            val member = memberRepository.save(Member.createSocialMember(socialLoginCommand.email))
            socialInfoRepository.save(SocialInfo(email = socialLoginCommand.email, socialType = socialLoginCommand.socialType, memberId = member.id))
        }
        return socialInfo
    }

    private fun createSocialMemberResponse(member: Member, hasNickname: Boolean): SocialMemberResponse {
        return SocialMemberResponse(
            id = member.id,
            email = member.email,
            nickname = member.nickname,
            profileUrl = member.profile?.url,
            token = JwtProvider.createToken(memberId = member.id, email = member.email, role = member.role),
            hasNickname = hasNickname
        )
    }
}

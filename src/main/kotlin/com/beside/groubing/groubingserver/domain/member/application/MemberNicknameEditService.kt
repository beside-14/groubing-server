package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import org.springframework.stereotype.Service

@Service
class MemberNicknameEditService(
    private val memberRepository: MemberRepository
) {
    fun edit(id: Long, nickname: String) {
        val member = memberRepository.findById(id).orElseThrow { MemberInputException("존재하지 않는 유저 입니다.") }
        // TODO(비속어 체크)
        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(nickname)) {
            throw MemberInputException("이미 사용 중인 닉네임 입니다.")
        }
        // 닉네임 수정
        member.editNickname(nickname)
    }
}

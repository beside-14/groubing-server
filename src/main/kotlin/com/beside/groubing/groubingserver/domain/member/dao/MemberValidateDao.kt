package com.beside.groubing.groubingserver.domain.member.dao

import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import org.springframework.stereotype.Repository

@Repository
class MemberValidateDao(
    private val memberRepository: MemberRepository
) {
    fun validateDuplicateNickname(nickname: String) {
        if (memberRepository.existsByNickname(nickname)) {
            throw MemberInputException("이미 사용 중인 닉네임 입니다.")
        }
    }

    fun validateDuplicateEmail(email: String) {
        if (memberRepository.existsByEmail(email)) {
            throw MemberInputException("이미 가입된 email 주소입니다.")
        }
    }
}

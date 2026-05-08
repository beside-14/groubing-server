package com.beside.groubing.domain.auth.domain

import com.beside.groubing.domain.member.domain.NewMember
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.member.exception.MemberInputException
import org.springframework.stereotype.Component

@Component
class SignUpValidator(
    private val memberQueryRepository: MemberQueryRepository
) {
    fun validate(newMember: NewMember) {
        if (newMember.email != null && memberQueryRepository.existsByEmail(newMember.email)) {
            throw MemberInputException("이미 가입된 email 주소입니다.")
        }
        if (memberQueryRepository.existsByNickname(newMember.nickname)) {
            throw MemberInputException("이미 사용 중인 닉네임 입니다.")
        }
    }
}

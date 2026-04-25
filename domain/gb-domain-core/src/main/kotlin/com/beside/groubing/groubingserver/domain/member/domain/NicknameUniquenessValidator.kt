package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import org.springframework.stereotype.Component

@Component
class NicknameUniquenessValidator(
    private val memberQueryRepository: MemberQueryRepository
) {
    fun validate(nickname: String) {
        if (memberQueryRepository.existsByNickname(nickname)) {
            throw MemberInputException("이미 사용 중인 닉네임 입니다.")
        }
    }
}

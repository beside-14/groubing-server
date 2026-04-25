package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.member.exception.MemberInputException
import org.springframework.stereotype.Component

@Component
class BingoBoardMembersValidator(
    private val memberQueryRepository: MemberQueryRepository
) {
    fun validate(memberIds: List<Long>) {
        if (memberQueryRepository.countByIdIn(memberIds) != memberIds.size) {
            throw MemberInputException("입력된 ID 중 존재하지 않는 회원이 있습니다. memberIds:$memberIds")
        }
    }
}

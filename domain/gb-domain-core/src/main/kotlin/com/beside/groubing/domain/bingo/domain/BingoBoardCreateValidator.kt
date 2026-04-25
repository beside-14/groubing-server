package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.payload.command.BingoBoardCreateCommand
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Component

@Component
class BingoBoardCreateValidator(
    private val memberQueryRepository: MemberQueryRepository
) {
    fun validate(command: BingoBoardCreateCommand) {
        memberQueryRepository.findById(command.memberId)
    }
}

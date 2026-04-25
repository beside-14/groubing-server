package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardCreateCommand
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Component

@Component
class BingoBoardCreateValidator(
    private val memberQueryRepository: MemberQueryRepository
) {
    fun validate(command: BingoBoardCreateCommand) {
        memberQueryRepository.findById(command.memberId)
    }
}

package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardCreateCommand
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoBoardCreateService(
    private val memberQueryRepository: MemberQueryRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository
) {
    fun create(command: BingoBoardCreateCommand): BingoBoard {
        memberQueryRepository.findById(command.memberId)
        return bingoBoardCommandRepository.save(command.toNewBingoBoard())
    }
}

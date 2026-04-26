package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.payload.command.BingoBoardCreateCommand
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoBoardCreateService(
    private val bingoBoardCommandRepository: BingoBoardCommandRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun create(command: BingoBoardCreateCommand): BingoBoard {
        memberQueryRepository.findById(command.memberId)
        return bingoBoardCommandRepository.save(command.toNewBingoBoard())
    }
}

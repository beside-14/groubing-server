package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.BingoBoardCreateValidator
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.payload.command.BingoBoardCreateCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BingoBoardCreateService(
    private val bingoBoardCommandRepository: BingoBoardCommandRepository,
    private val bingoBoardCreateValidator: BingoBoardCreateValidator
) {
    fun create(command: BingoBoardCreateCommand): BingoBoard {
        bingoBoardCreateValidator.validate(command)
        return bingoBoardCommandRepository.save(command.toNewBingoBoard())
    }
}

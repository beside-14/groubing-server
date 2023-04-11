package com.beside.groubing.groubingserver.domain.bingo.exception

class BingoInputException(
    override val message: String
) : RuntimeException("[BingoInputException] $message")

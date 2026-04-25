package com.beside.groubing.domain.bingo.exception

class BingoInputException(
    override val message: String
) : RuntimeException("[BingoInputException] $message")

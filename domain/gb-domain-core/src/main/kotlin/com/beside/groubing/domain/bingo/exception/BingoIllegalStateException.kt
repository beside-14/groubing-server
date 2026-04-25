package com.beside.groubing.domain.bingo.exception

class BingoIllegalStateException(
    override val message: String
) : RuntimeException("[BingoIllegalStateException] $message")

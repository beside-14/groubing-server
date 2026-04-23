package com.beside.groubing.groubingserver.domain.bingo.exception

class BingoIllegalStateException(
    override val message: String
) : RuntimeException("[BingoIllegalStateException] $message")

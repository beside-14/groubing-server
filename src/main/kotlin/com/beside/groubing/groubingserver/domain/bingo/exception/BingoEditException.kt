package com.beside.groubing.groubingserver.domain.bingo.exception

class BingoEditException(
    override val message: String
) : RuntimeException("[BingoEditException] $message")

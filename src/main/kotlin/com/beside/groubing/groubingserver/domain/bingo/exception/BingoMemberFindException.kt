package com.beside.groubing.groubingserver.domain.bingo.exception

class BingoMemberFindException(
    override val message: String
) : RuntimeException("[BingoMemberFindException] $message")

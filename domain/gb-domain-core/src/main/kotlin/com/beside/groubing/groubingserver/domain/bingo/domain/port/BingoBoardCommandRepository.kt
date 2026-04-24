package com.beside.groubing.groubingserver.domain.bingo.domain.port

interface BingoBoardCommandRepository {
    fun inactivateAllOf(memberId: Long)
}

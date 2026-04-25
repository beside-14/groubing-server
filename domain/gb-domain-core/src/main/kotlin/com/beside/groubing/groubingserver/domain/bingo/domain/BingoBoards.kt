package com.beside.groubing.groubingserver.domain.bingo.domain

class BingoBoards(val data: List<BingoBoard>) {
    fun visibleTo(viewerMemberId: Long, ownerMemberId: Long): List<BingoBoard> {
        if (viewerMemberId == ownerMemberId) {
            return data
        }
        return data.filter { it.isStarted() }
    }
}

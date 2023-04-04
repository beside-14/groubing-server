package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.payload.command.CreateBingoCommand
import com.beside.groubing.groubingserver.domain.member.domain.Member
import org.springframework.stereotype.Component

@Component
class BingoBoardMapper {

    fun toBingoBoard(command: CreateBingoCommand): BingoBoard =
        BingoBoard(
            title = command.title,
            type = command.type,
            size = command.size,
            color = command.color,
            goal = command.goal,
            open = command.open,
            since = command.since,
            until = command.until,
            member = Member(id = command.memberId)
        )
}

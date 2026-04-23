package com.beside.groubing.groubingserver.domain.bingo.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BingoBoardRepository : JpaRepository<BingoBoard, Long> {
    fun findByIdAndActiveIsTrue(id: Long): Optional<BingoBoard>
}

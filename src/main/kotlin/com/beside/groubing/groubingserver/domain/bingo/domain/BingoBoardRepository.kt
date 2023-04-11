package com.beside.groubing.groubingserver.domain.bingo.domain

import org.springframework.data.jpa.repository.JpaRepository

interface BingoBoardRepository : JpaRepository<BingoBoard, Long> {
}

package com.beside.groubing.groubingserver.domain.bingo.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BingoItemRepository : JpaRepository<BingoItem, Long>

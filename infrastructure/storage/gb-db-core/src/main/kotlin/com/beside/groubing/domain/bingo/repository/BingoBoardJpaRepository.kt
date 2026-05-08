package com.beside.groubing.domain.bingo.repository

import com.beside.groubing.domain.bingo.entity.BingoBoardEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BingoBoardJpaRepository : JpaRepository<BingoBoardEntity, Long> {
    fun findByIdAndActiveIsTrue(id: Long): Optional<BingoBoardEntity>
}

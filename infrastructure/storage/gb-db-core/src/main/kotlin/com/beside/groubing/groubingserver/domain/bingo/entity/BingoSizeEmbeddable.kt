package com.beside.groubing.groubingserver.domain.bingo.entity

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize
import jakarta.persistence.Embeddable

@Embeddable
class BingoSizeEmbeddable(
    val size: Int
) {
    fun toDomain(): BingoSize = BingoSize.cache(size)

    companion object {
        fun from(domain: BingoSize): BingoSizeEmbeddable = BingoSizeEmbeddable(domain.size)
    }
}

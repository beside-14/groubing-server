package com.beside.groubing.groubingserver.domain.bingo.entity

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoPeriod
import jakarta.persistence.Embeddable
import java.time.LocalDate

@Embeddable
class BingoPeriodEmbeddable(
    val since: LocalDate,
    val until: LocalDate
) {
    fun toDomain(): BingoPeriod = BingoPeriod.create(since, until)

    companion object {
        fun from(domain: BingoPeriod): BingoPeriodEmbeddable = BingoPeriodEmbeddable(domain.since, domain.until)
    }
}

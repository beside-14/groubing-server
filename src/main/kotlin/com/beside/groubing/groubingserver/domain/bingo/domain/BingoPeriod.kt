package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import jakarta.persistence.Embeddable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Embeddable
class BingoPeriod private constructor(
    val since: LocalDate,
    val until: LocalDate
) {
    init {
        val now = LocalDate.now()
        if (until.isBefore(now)) throw BingoInputException("빙고 종료일은 현재 과거일 수 없습니다.")
        if (until.isBefore(since)) throw BingoInputException("빙고 종료일은 시작일보다 과거일 수 없습니다.")
    }

    fun calculateLeftDays(): Long = LocalDate.now().until(until, ChronoUnit.DAYS)

    companion object {
        fun create(since: LocalDate, until: LocalDate): BingoPeriod =
            BingoPeriod(since, until)
    }
}

package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.exception.BingoInputException
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BingoPeriod private constructor(
    val since: LocalDate,
    val until: LocalDate
) {
    fun calculateLeftDays(): Long = LocalDate.now().until(until, ChronoUnit.DAYS)

    fun isExpired(): Boolean = LocalDate.now().isAfter(until)

    companion object {
        fun create(since: LocalDate, until: LocalDate): BingoPeriod {
            val now = LocalDate.now()
            if (until.isBefore(now)) throw BingoInputException("빙고 종료일은 현재 과거일 수 없습니다.")
            if (until.isBefore(since)) throw BingoInputException("빙고 종료일은 시작일보다 과거일 수 없습니다.")
            return BingoPeriod(since, until)
        }

        fun of(since: LocalDate, until: LocalDate): BingoPeriod = BingoPeriod(since, until)
    }
}

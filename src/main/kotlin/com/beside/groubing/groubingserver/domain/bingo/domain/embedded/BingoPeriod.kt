package com.beside.groubing.groubingserver.domain.bingo.domain.embedded

import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import jakarta.persistence.Embeddable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Embeddable
class BingoPeriod private constructor(
    var since: LocalDate,
    var until: LocalDate
) {
    init {
        if (until.isBefore(since)) throw BingoInputException("빙고 종료일은 시작일보다 과거일 수 없습니다.")
        if (since.isEqual(until)) throw BingoInputException("빙고 시작일과 종료일이 같을 수 없습니다.")
    }

    fun calculateLeftDays(): Long = LocalDate.now().until(until, ChronoUnit.DAYS)

    companion object {
        fun createBingoPeriod(since: LocalDate, until: LocalDate): BingoPeriod = BingoPeriod(since, until)
    }
}

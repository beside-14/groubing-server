package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Embeddable
class BingoPeriod private constructor(
    @Column
    var since: LocalDate,
    @Column
    var until: LocalDate
) {
    init {
        val now = LocalDate.now()
        if (since.isBefore(now)) throw BingoInputException("빙고 시작일은 현재보다 과거일 수 없습니다.")
        if (since.isEqual(until)) throw BingoInputException("빙고 시작일과 종료일은 같은 수 없습니다.")
        if (until.isBefore(now)) throw BingoInputException("빙고 종료일은 현재 과거일 수 없습니다.")
        if (until.isBefore(since)) throw BingoInputException("빙고 종료일은 시작일보다 과거일 수 없습니다.")
        if (since.isEqual(until)) throw BingoInputException("빙고 시작일과 종료일이 같을 수 없습니다.")
    }

    fun calculateLeftDays(): Long = LocalDate.now().until(until, ChronoUnit.DAYS)

    companion object {
        fun create(since: LocalDate, until: LocalDate): BingoPeriod =
            BingoPeriod(since, until)
    }
}

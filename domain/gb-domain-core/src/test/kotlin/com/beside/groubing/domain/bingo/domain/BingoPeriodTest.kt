package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.exception.BingoInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class BingoPeriodTest : BehaviorSpec({
    val today = LocalDate.now()

    Given("BingoPeriod.create 로 기간을 생성할 때") {
        When("시작일이 오늘, 종료일이 미래이면") {
            val period = BingoPeriod.create(today, today.plusDays(7))

            Then("정상적으로 생성된다") {
                period.since shouldBe today
                period.until shouldBe today.plusDays(7)
            }
        }

        When("시작일과 종료일이 모두 오늘이면(경계값)") {
            val period = BingoPeriod.create(today, today)

            Then("정상적으로 생성된다") {
                period.since shouldBe today
                period.until shouldBe today
            }
        }

        When("시작일이 과거이지만 종료일이 미래이면") {
            val period = BingoPeriod.create(today.minusDays(3), today.plusDays(3))

            Then("정상적으로 생성된다 (시작일은 과거여도 허용)") {
                period.since shouldBe today.minusDays(3)
                period.until shouldBe today.plusDays(3)
            }
        }

        When("종료일이 과거이면") {
            Then("BingoInputException 이 발생한다 - 종료일은 과거일 수 없음") {
                shouldThrow<BingoInputException> {
                    BingoPeriod.create(today.minusDays(10), today.minusDays(1))
                }.message shouldBe "빙고 종료일은 현재 과거일 수 없습니다."
            }
        }

        When("종료일이 시작일보다 과거이면(기간 역전)") {
            Then("BingoInputException 이 발생한다 - 종료일은 시작일보다 과거일 수 없음") {
                shouldThrow<BingoInputException> {
                    BingoPeriod.create(today.plusDays(10), today.plusDays(5))
                }.message shouldBe "빙고 종료일은 시작일보다 과거일 수 없습니다."
            }
        }
    }

    Given("BingoPeriod.of 는 검증 없이 재구성한다") {
        When("종료일이 시작일보다 과거인 값으로 of 를 호출하면") {
            val period = BingoPeriod.of(today.plusDays(10), today.plusDays(5))

            Then("예외 없이 그대로 생성된다") {
                period.since shouldBe today.plusDays(10)
                period.until shouldBe today.plusDays(5)
            }
        }
    }

    Given("isExpired 판정") {
        When("종료일이 미래이면") {
            val period = BingoPeriod.of(today, today.plusDays(1))

            Then("만료되지 않았다") {
                period.isExpired() shouldBe false
            }
        }

        When("종료일이 오늘이면") {
            val period = BingoPeriod.of(today, today)

            Then("아직 만료되지 않았다 (오늘 이후부터 만료)") {
                period.isExpired() shouldBe false
            }
        }

        When("종료일이 과거이면") {
            val period = BingoPeriod.of(today.minusDays(5), today.minusDays(1))

            Then("만료되었다") {
                period.isExpired() shouldBe true
            }
        }
    }

    Given("calculateLeftDays 계산") {
        When("종료일이 7일 뒤이면") {
            val period = BingoPeriod.of(today, today.plusDays(7))

            Then("남은 일수는 7일이다") {
                period.calculateLeftDays() shouldBe 7L
            }
        }

        When("종료일이 오늘이면") {
            val period = BingoPeriod.of(today, today)

            Then("남은 일수는 0일이다") {
                period.calculateLeftDays() shouldBe 0L
            }
        }

        When("종료일이 과거(2일 전)이면") {
            val period = BingoPeriod.of(today.minusDays(5), today.minusDays(2))

            Then("남은 일수는 음수(-2일)이다") {
                period.calculateLeftDays() shouldBe -2L
            }
        }
    }
})

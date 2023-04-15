package com.beside.groubing.groubingserver.domain.bingo.validator

import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardCreateRequest
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class BingoBoardCreateValidator : Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return BingoBoardCreateRequest::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        val request = target as BingoBoardCreateRequest

        if (request.bingoSize != 3 && request.bingoSize != 4) {
            errors.rejectValue("bingoSize", "빙고 사이즈", "3X3 혹은 4X4 사이즈만 가능합니다.")
            return
        }
        if (request.bingoSize == 3 && request.goal !in (1..3)) {
            errors.rejectValue("goal", "목표수", "목표는 3개 이내로 설정해 주세요.")
            return
        }
        if (request.bingoSize == 4 && request.goal !in (1..4)) {
            errors.rejectValue("goal", "목표수", "목표는 4개 이내로 설정해 주세요.")
            return
        }
        if (request.until.isBefore(request.since)) {
            errors.rejectValue("until", "빙고 종료일", "빙고 종료일은 시작일보다 과거일 수 없습니다.")
        }
    }
}

package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.aMember
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

class MemberTest : ExpectSpec({
    context("이메일의 특정 영역을 * 로 치환한다.") {
        expect("username 의 길이가 짝수인 경우") {
            val oldEmail = "testtesttesttest@groubing.com"
            val newEmail = "testtest********@groubing.com"
            val member = aMember(email = oldEmail)
            val replaceEmail = member.maskEmail()
            replaceEmail shouldBe newEmail
        }

        expect("username 의 길이가 홀수인 경우") {
            val oldEmail = "testtesttesttes@groubing.com"
            val newEmail = "testtes********@groubing.com"
            val member = aMember(email = oldEmail)
            val replaceEmail = member.maskEmail()
            replaceEmail shouldBe newEmail
        }
    }
})

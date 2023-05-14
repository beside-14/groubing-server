package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.aMember
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

class MemberTest : ExpectSpec({
    expect("이메일의 특정 영역을 * 로 치환한다.") {
        val oldEmail = "test1234@groubing.com"
        val newEmail = oldEmail.replace("1234", "****")
        val member = aMember(email = oldEmail)
        val replaceEmail = member.maskEmail()
        replaceEmail shouldBe newEmail
    }
})

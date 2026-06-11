package com.beside.groubing.domain.member.repository

import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.domain.NewMember
import com.beside.groubing.domain.member.exception.MemberInputException
import com.beside.groubing.persistence.PersistenceTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@PersistenceTest
@Import(MemberRepositoryAdapter::class)
class MemberRepositoryAdapterTest(
    private val memberRepositoryAdapter: MemberRepositoryAdapter
) : FunSpec({

    fun newMember(loginId: String?, nickname: String) = NewMember(
        loginId = loginId,
        password = "password",
        nickname = nickname,
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC
    )

    test("회원을 저장하면 id가 채번된다") {
        val saved = memberRepositoryAdapter.save(newMember(loginId = "user1", nickname = "nick1"))

        (saved.id > 0L) shouldBe true
    }

    test("중복된 loginId 저장 시 DataIntegrityViolationException 을 MemberInputException 으로 번역한다") {
        memberRepositoryAdapter.save(newMember(loginId = "dupId", nickname = "nickA"))

        val exception = shouldThrow<MemberInputException> {
            memberRepositoryAdapter.save(newMember(loginId = "dupId", nickname = "nickB"))
        }
        exception.message shouldBe "중복된 아이디 / 닉네임 입니다."
    }

    test("중복된 nickname 저장 시 DataIntegrityViolationException 을 MemberInputException 으로 번역한다") {
        memberRepositoryAdapter.save(newMember(loginId = "idA", nickname = "dupNick"))

        shouldThrow<MemberInputException> {
            memberRepositoryAdapter.save(newMember(loginId = "idB", nickname = "dupNick"))
        }
    }
})

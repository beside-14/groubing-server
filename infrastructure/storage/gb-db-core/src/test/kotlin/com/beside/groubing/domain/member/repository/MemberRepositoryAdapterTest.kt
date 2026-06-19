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
import java.time.LocalDateTime

@PersistenceTest
@Import(MemberRepositoryAdapter::class)
class MemberRepositoryAdapterTest(
    private val memberRepositoryAdapter: MemberRepositoryAdapter,
    private val memberJpaRepository: MemberJpaRepository
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

    test("withdraw 하면 회원이 비활성화되고 deletedAt 이 기록된다") {
        val saved = memberRepositoryAdapter.save(newMember(loginId = "leaver", nickname = "leaverNick"))
        val now = LocalDateTime.now()

        memberRepositoryAdapter.withdraw(saved.id, now)

        val entity = memberJpaRepository.findById(saved.id).orElseThrow()
        entity.active shouldBe false
        entity.deletedAt shouldBe now
    }

    test("탈퇴한 회원은 findById 로는 조회되지만 findActiveById 로는 조회되지 않는다") {
        val saved = memberRepositoryAdapter.save(newMember(loginId = "wd", nickname = "wdNick"))
        memberRepositoryAdapter.withdraw(saved.id, LocalDateTime.now())

        memberRepositoryAdapter.findById(saved.id).active shouldBe false
        shouldThrow<MemberInputException> {
            memberRepositoryAdapter.findActiveById(saved.id)
        }.message shouldBe "존재하지 않는 유저 입니다."
    }
})

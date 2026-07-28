package com.beside.groubing.domain.member.repository

import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.domain.NewMember
import com.beside.groubing.domain.member.exception.MemberInputException
import com.beside.groubing.persistence.PersistenceTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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

    test("findAll 은 탈퇴한 회원도 포함하지만 findAllActive 는 활성 회원만 조회한다") {
        val active = memberRepositoryAdapter.save(newMember(loginId = "act", nickname = "actNick"))
        val withdrawn = memberRepositoryAdapter.save(newMember(loginId = "wd2", nickname = "wd2Nick"))
        memberRepositoryAdapter.withdraw(withdrawn.id, LocalDateTime.now())

        val ids = listOf(active.id, withdrawn.id)
        memberRepositoryAdapter.findAll(ids).map { it.id } shouldContainExactlyInAnyOrder ids
        memberRepositoryAdapter.findAllActive(ids).map { it.id } shouldBe listOf(active.id)
    }

    test("tombstone 하면 식별정보가 제거되고 cleanedAt 이 기록된다") {
        val saved = memberRepositoryAdapter.save(newMember(loginId = "ts", nickname = "tsNick"))
        memberRepositoryAdapter.withdraw(saved.id, LocalDateTime.now())

        memberRepositoryAdapter.tombstone(saved.id, LocalDateTime.now())

        val entity = memberJpaRepository.findById(saved.id).orElseThrow()
        entity.loginId shouldBe null
        entity.nickname shouldBe "탈퇴회원_${saved.id}"
        entity.password shouldBe ""
        entity.fcmToken shouldBe null
        entity.cleanedAt shouldNotBe null
    }

    test("hardDelete 하면 회원 row 가 완전히 삭제되고 이전 프로필이 반환된다") {
        val saved = memberRepositoryAdapter.save(newMember(loginId = "hd", nickname = "hdNick"))

        val previousProfile = memberRepositoryAdapter.hardDelete(saved.id)

        memberJpaRepository.findById(saved.id).isPresent shouldBe false
        previousProfile shouldBe null
    }

    test("findExpiredMemberIds 는 기준 이전 탈퇴 + 미정리(cleanedAt null) 회원만 반환한다") {
        val expired = memberRepositoryAdapter.save(newMember(loginId = "exp", nickname = "expNick"))
        val recent = memberRepositoryAdapter.save(newMember(loginId = "rec", nickname = "recNick"))
        val cleaned = memberRepositoryAdapter.save(newMember(loginId = "cln", nickname = "clnNick"))
        memberRepositoryAdapter.withdraw(expired.id, LocalDateTime.now().minusDays(400))
        memberRepositoryAdapter.withdraw(recent.id, LocalDateTime.now().minusDays(10))
        memberRepositoryAdapter.withdraw(cleaned.id, LocalDateTime.now().minusDays(400))
        memberRepositoryAdapter.tombstone(cleaned.id, LocalDateTime.now())

        val result = memberRepositoryAdapter.findExpiredMemberIds(LocalDateTime.now().minusDays(365))

        result shouldContain expired.id
        result shouldNotContain recent.id
        result shouldNotContain cleaned.id
    }
})

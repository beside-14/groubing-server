package com.beside.groubing.domain.bingo.repository

import com.beside.groubing.aEmptyBingo
import com.beside.groubing.aEnglishStudyBingoBoard
import com.beside.groubing.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.domain.bingo.domain.BingoGoal
import com.beside.groubing.domain.bingo.domain.BingoMember
import com.beside.groubing.domain.bingo.domain.BingoMemberType
import com.beside.groubing.domain.bingo.domain.BingoPeriod
import com.beside.groubing.domain.bingo.domain.BingoSize
import com.beside.groubing.domain.bingo.entity.BingoBoardEntity
import com.beside.groubing.global.config.QuerydslConfig
import com.beside.groubing.persistence.PersistenceTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import java.time.LocalDate

@PersistenceTest
@Import(BingoBoardRepositoryAdapter::class, BingoBoardListFindDao::class, QuerydslConfig::class)
class BingoBoardRepositoryAdapterTest(
    private val bingoBoardRepositoryAdapter: BingoBoardRepositoryAdapter,
    private val bingoBoardJpaRepository: BingoBoardJpaRepository,
    private val testEntityManager: TestEntityManager
) : DescribeSpec({

    describe("updateBase") {
        context("리더가 기본정보(제목/목표/기간)를 변경하면") {
            it("해당 보드의 제목/목표/기간만 갱신되고 멤버/아이템은 그대로 유지된다") {
                val saved = bingoBoardJpaRepository.save(BingoBoardEntity.from(aEnglishStudyBingoBoard()))
                val savedId = saved.id
                val originalMemberCount = saved.bingoMembers.size
                val originalActiveCount = saved.bingoMembers.count { it.active }
                val originalItemTitles = saved.bingoItems.sortedBy { it.id }.map { it.title }
                val newTitle = "변경된 제목"
                val newGoal = 5
                val newSince = LocalDate.now().plusDays(1)
                val newUntil = LocalDate.now().plusDays(10)

                bingoBoardRepositoryAdapter.updateBase(
                    savedId,
                    newTitle,
                    BingoGoal.create(newGoal, BingoSize.cache(3)),
                    BingoPeriod.create(newSince, newUntil)
                )
                testEntityManager.flush()
                testEntityManager.clear()

                val reloaded = bingoBoardJpaRepository.findById(savedId).orElseThrow()
                reloaded.title shouldBe newTitle
                reloaded.bingoGoal.goal shouldBe newGoal
                reloaded.period!!.since shouldBe newSince
                reloaded.period!!.until shouldBe newUntil
                reloaded.bingoMembers.size shouldBe originalMemberCount
                reloaded.bingoMembers.count { it.active } shouldBe originalActiveCount
                reloaded.bingoItems.size shouldBe originalItemTitles.size
                reloaded.bingoItems.sortedBy { it.id }.map { it.title } shouldBe originalItemTitles
            }
        }
    }

    describe("addBingoMembers") {
        context("리더가 멤버를 추가하면") {
            it("추가 멤버만 PARTICIPANT 로 INSERT 되고 기존 멤버/아이템은 그대로 유지된다") {
                val saved = bingoBoardJpaRepository.save(BingoBoardEntity.from(aEmptyBingo()))
                val savedId = saved.id
                val originalMemberCount = saved.bingoMembers.size
                val originalItemTitles = saved.bingoItems.sortedBy { it.id }.map { it.title }

                bingoBoardRepositoryAdapter.addBingoMembers(
                    savedId,
                    listOf(BingoMember.create(2L), BingoMember.create(3L))
                )
                testEntityManager.flush()
                testEntityManager.clear()

                val reloaded = bingoBoardJpaRepository.findById(savedId).orElseThrow()
                reloaded.bingoMembers.size shouldBe originalMemberCount + 2
                reloaded.bingoMembers.map { it.memberId } shouldContainAll listOf(2L, 3L)
                reloaded.bingoMembers.filter { it.memberId in setOf(2L, 3L) }.forEach {
                    it.active shouldBe true
                    it.bingoMemberType shouldBe BingoMemberType.PARTICIPANT
                }
                reloaded.bingoItems.size shouldBe originalItemTitles.size
                reloaded.bingoItems.sortedBy { it.id }.map { it.title } shouldBe originalItemTitles
            }
        }
    }

    describe("updatePeriod") {
        context("리더가 기간을 변경하면") {
            it("기간만 갱신되고 멤버/아이템은 그대로 유지된다") {
                val saved = bingoBoardJpaRepository.save(BingoBoardEntity.from(aEnglishStudyBingoBoard()))
                val savedId = saved.id
                val originalMemberCount = saved.bingoMembers.size
                val originalItemTitles = saved.bingoItems.sortedBy { it.id }.map { it.title }
                val newSince = LocalDate.now().plusDays(2)
                val newUntil = LocalDate.now().plusDays(20)

                bingoBoardRepositoryAdapter.updatePeriod(savedId, BingoPeriod.create(newSince, newUntil))
                testEntityManager.flush()
                testEntityManager.clear()

                val reloaded = bingoBoardJpaRepository.findById(savedId).orElseThrow()
                reloaded.period!!.since shouldBe newSince
                reloaded.period!!.until shouldBe newUntil
                reloaded.bingoMembers.size shouldBe originalMemberCount
                reloaded.bingoItems.size shouldBe originalItemTitles.size
                reloaded.bingoItems.sortedBy { it.id }.map { it.title } shouldBe originalItemTitles
            }
        }
    }

    describe("deactivateBingoMember") {
        context("참여자가 빙고를 나가면") {
            it("해당 멤버와 그의 완료기록만 비활성화되고 다른 멤버/아이템은 그대로 유지된다") {
                val board = aEnglishStudyBingoBoard()
                board.completeBingoItem(bingoItemId = 1L, memberId = 2L)
                val saved = bingoBoardJpaRepository.save(BingoBoardEntity.from(board))
                val savedId = saved.id
                val originalMemberCount = saved.bingoMembers.size
                val originalItemTitles = saved.bingoItems.sortedBy { it.id }.map { it.title }

                bingoBoardRepositoryAdapter.deactivateBingoMember(savedId, 2L)
                testEntityManager.flush()
                testEntityManager.clear()

                val reloaded = bingoBoardJpaRepository.findById(savedId).orElseThrow()
                reloaded.bingoMembers.first { it.memberId == 2L }.active shouldBe false
                reloaded.bingoMembers.filter { it.memberId != 2L }.forEach { it.active shouldBe true }
                val completeMembersOfLeaver = reloaded.bingoItems.flatMap { it.completeMembers }.filter { it.memberId == 2L }
                completeMembersOfLeaver.shouldNotBeEmpty()
                completeMembersOfLeaver.forEach { it.active shouldBe false }
                reloaded.bingoMembers.size shouldBe originalMemberCount
                reloaded.bingoItems.size shouldBe originalItemTitles.size
                reloaded.bingoItems.sortedBy { it.id }.map { it.title } shouldBe originalItemTitles
            }
        }
    }

    describe("updateMemo") {
        context("리더가 메모를 변경하면") {
            it("메모만 갱신되고 멤버/아이템은 그대로 유지된다") {
                val saved = bingoBoardJpaRepository.save(BingoBoardEntity.from(aEnglishStudyBingoBoard()))
                val savedId = saved.id
                val originalMemberCount = saved.bingoMembers.size
                val originalActiveCount = saved.bingoMembers.count { it.active }
                val originalItemTitles = saved.bingoItems.sortedBy { it.id }.map { it.title }
                val newMemo = "새 메모"

                bingoBoardRepositoryAdapter.updateMemo(savedId, newMemo)
                testEntityManager.flush()
                testEntityManager.clear()

                val reloaded = bingoBoardJpaRepository.findById(savedId).orElseThrow()
                reloaded.memo shouldBe newMemo
                reloaded.bingoMembers.size shouldBe originalMemberCount
                reloaded.bingoMembers.count { it.active } shouldBe originalActiveCount
                reloaded.bingoItems.size shouldBe originalItemTitles.size
                reloaded.bingoItems.sortedBy { it.id }.map { it.title } shouldBe originalItemTitles
            }
        }
    }

    describe("updateOpen") {
        context("리더가 공개여부를 변경하면") {
            it("공개여부만 토글되고 멤버/아이템은 그대로 유지된다") {
                val saved = bingoBoardJpaRepository.save(BingoBoardEntity.from(aEnglishStudyBingoBoard()))
                val savedId = saved.id
                val originalOpen = saved.open
                val originalMemberCount = saved.bingoMembers.size
                val originalActiveCount = saved.bingoMembers.count { it.active }
                val originalItemTitles = saved.bingoItems.sortedBy { it.id }.map { it.title }

                bingoBoardRepositoryAdapter.updateOpen(savedId, !originalOpen)
                testEntityManager.flush()
                testEntityManager.clear()

                val reloaded = bingoBoardJpaRepository.findById(savedId).orElseThrow()
                reloaded.open shouldBe !originalOpen
                reloaded.bingoMembers.size shouldBe originalMemberCount
                reloaded.bingoMembers.count { it.active } shouldBe originalActiveCount
                reloaded.bingoItems.size shouldBe originalItemTitles.size
                reloaded.bingoItems.sortedBy { it.id }.map { it.title } shouldBe originalItemTitles
            }
        }
    }
})

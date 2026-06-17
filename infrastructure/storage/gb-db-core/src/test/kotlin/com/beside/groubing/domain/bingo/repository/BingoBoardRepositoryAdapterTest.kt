package com.beside.groubing.domain.bingo.repository

import com.beside.groubing.aEnglishStudyBingoBoard
import com.beside.groubing.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.domain.bingo.entity.BingoBoardEntity
import com.beside.groubing.global.config.QuerydslConfig
import com.beside.groubing.persistence.PersistenceTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import

@PersistenceTest
@Import(BingoBoardRepositoryAdapter::class, BingoBoardListFindDao::class, QuerydslConfig::class)
class BingoBoardRepositoryAdapterTest(
    private val bingoBoardRepositoryAdapter: BingoBoardRepositoryAdapter,
    private val bingoBoardJpaRepository: BingoBoardJpaRepository,
    private val testEntityManager: TestEntityManager
) : DescribeSpec({

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
})

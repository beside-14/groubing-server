package com.beside.groubing.domain.bingo.dao

import com.beside.groubing.aEnglishStudyBingoBoard
import com.beside.groubing.aGameBingoBoard
import com.beside.groubing.aHealthBingoBoard
import com.beside.groubing.global.config.QuerydslConfig
import com.beside.groubing.domain.bingo.entity.BingoBoardEntity
import com.beside.groubing.domain.bingo.repository.BingoBoardJpaRepository
import com.beside.groubing.persistence.PersistenceTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@PersistenceTest
@Import(QuerydslConfig::class, BingoBoardListFindDao::class)
class BingoBoardListFindDaoTest(
    private val bingoBoardListFindDao: BingoBoardListFindDao,

    private val bingoBoardJpaRepository: BingoBoardJpaRepository
) : DescribeSpec({
    var englishStudyBoardId = 0L

    beforeTest {
        val saved = bingoBoardJpaRepository.saveAll(
            listOf(
                BingoBoardEntity.from(aEnglishStudyBingoBoard()),
                BingoBoardEntity.from(aHealthBingoBoard()),
                BingoBoardEntity.from(aGameBingoBoard())
            )
        )
        englishStudyBoardId = saved.first().id
    }

    listOf(
        Pair(3L, 2)
    ).forEach { (memberId, expectedSize) ->
        it("should return bingo board list for a memberId : $memberId") {
            val result = bingoBoardListFindDao.find(memberId)

            result.shouldNotBeEmpty()
            result.size shouldBe expectedSize
        }
    }

    describe("isLeaderOf") {
        it("리더이면 true 를 반환한다") {
            bingoBoardListFindDao.isLeaderOf(englishStudyBoardId, memberId = 1L).shouldBeTrue()
        }

        it("참여자이면 false 를 반환한다") {
            bingoBoardListFindDao.isLeaderOf(englishStudyBoardId, memberId = 3L).shouldBeFalse()
        }

        it("멤버가 아니면 false 를 반환한다") {
            bingoBoardListFindDao.isLeaderOf(englishStudyBoardId, memberId = 99L).shouldBeFalse()
        }

        it("존재하지 않는 빙고보드이면 false 를 반환한다") {
            bingoBoardListFindDao.isLeaderOf(bingoBoardId = 999L, memberId = 1L).shouldBeFalse()
        }
    }
})

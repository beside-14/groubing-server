package com.beside.groubing.groubingserver.domain.bingo.dao

import com.beside.groubing.groubingserver.aEnglishStudyBingoBoard
import com.beside.groubing.groubingserver.aGameBingoBoard
import com.beside.groubing.groubingserver.aHealthBingoBoard
import com.beside.groubing.groubingserver.config.QuerydslConfig
import com.beside.groubing.groubingserver.domain.bingo.entity.BingoBoardEntity
import com.beside.groubing.groubingserver.domain.bingo.repository.BingoBoardJpaRepository
import com.beside.groubing.groubingserver.persistence.LocalPersistenceTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@LocalPersistenceTest
@Import(QuerydslConfig::class, BingoBoardListFindDao::class)
class BingoBoardListFindDaoTest(
    private val bingoBoardListFindDao: BingoBoardListFindDao,

    private val bingoBoardJpaRepository: BingoBoardJpaRepository
) : DescribeSpec({
    beforeTest {
        bingoBoardJpaRepository.saveAll(
            listOf(
                BingoBoardEntity.from(aEnglishStudyBingoBoard()),
                BingoBoardEntity.from(aHealthBingoBoard()),
                BingoBoardEntity.from(aGameBingoBoard())
            )
        )
    }

    listOf(
        Pair(3L, 2)
    ).forEach { (memberId, expectedSize) ->
        it("should return bingo board list for a memberId : $memberId") {
            val result = bingoBoardListFindDao.findBingoBoardList(memberId)

            result.shouldNotBeEmpty()
            result.size shouldBe expectedSize
        }
    }
})

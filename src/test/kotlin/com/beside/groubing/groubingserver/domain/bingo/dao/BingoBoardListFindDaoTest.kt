package com.beside.groubing.groubingserver.domain.bingo.dao

import com.beside.groubing.groubingserver.aEnglishStudyBingoBoard
import com.beside.groubing.groubingserver.aGameBingoBoard
import com.beside.groubing.groubingserver.aHealthBingoBoard
import com.beside.groubing.groubingserver.config.QuerydslConfig
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.persistence.LocalPersistenceTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@LocalPersistenceTest
@Import(QuerydslConfig::class, BingoBoardListFindDao::class)
class BingoBoardListFindDaoTest(
    private val bingoBoardListFindDao: BingoBoardListFindDao,

    private val bingoBoardRepository: BingoBoardRepository
) : DescribeSpec({
    beforeTest {
        bingoBoardRepository.saveAll(listOf(
            aEnglishStudyBingoBoard(),
            aHealthBingoBoard(),
            aGameBingoBoard()
        ))
    }

    listOf(
        Pair(1L, 3),
        Pair(3L, 2)
    ).forEach { (memberId, expectedSize) ->
        it("should return bingo board list for a memberId : $memberId") {
            // 테스트 실행
            val result = bingoBoardListFindDao.findBingoBoardList(memberId)

            // 결과 검증
            result.shouldNotBeEmpty()
            result.size shouldBe expectedSize
        }
    }
})

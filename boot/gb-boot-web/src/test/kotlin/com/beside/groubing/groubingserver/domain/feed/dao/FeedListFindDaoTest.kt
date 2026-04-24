package com.beside.groubing.groubingserver.domain.feed.dao

import com.beside.groubing.groubingserver.aMember
import com.beside.groubing.groubingserver.config.QuerydslConfig
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoItemUpdateCommand
import com.beside.groubing.groubingserver.domain.member.entity.MemberEntity
import com.beside.groubing.groubingserver.domain.member.repository.MemberJpaRepository
import com.beside.groubing.groubingserver.persistence.LocalPersistenceTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.springframework.context.annotation.Import

@LocalPersistenceTest
@Import(QuerydslConfig::class, FeedListFindDao::class)
class FeedListFindDaoTest(
    private val feedListFindDao: FeedListFindDao,

    private val memberRepository: MemberJpaRepository,

    private val bingoBoardRepository: BingoBoardRepository
) : FunSpec({

    var englishBoardId = 1L
    var healthBoardId = 2L
    var gameBoardId = 3L

    var members: List<MemberEntity> = listOf()

    beforeEach {
        memberRepository.saveAll((1L..50L).map { aMember(it) })
        members = memberRepository.findAll()

        val savedEnglishBoard = bingoBoardRepository.save(BingoBoard.create(members[0].id, "영어", 3, BingoBoardType.GROUP, true, 3))
        val savedHealthBoard = bingoBoardRepository.save(BingoBoard.create(members[0].id, "운동", 3, BingoBoardType.GROUP, true, 3))
        val savedGameBoard = bingoBoardRepository.save(BingoBoard.create(members[0].id, "헬스", 3, BingoBoardType.GROUP, true, 3))

        savedEnglishBoard.bingoItems.forEachIndexed { index, bingoItem ->
            val command = BingoItemUpdateCommand.createCommand("영어 ${index + 1}", "2023년 ${index + 1}월달까지 영어")
            savedEnglishBoard.updateBingoItem(memberId = members[0].id, bingoItemId = bingoItem.id, title = command.title, subTitle = command.subTitle)
        }

        savedHealthBoard.bingoItems.forEachIndexed { index, bingoItem ->
            val command = BingoItemUpdateCommand.createCommand("운동 ${index + 1}", "2023년 ${index + 1}월달까지 운동")
            savedHealthBoard.updateBingoItem(memberId = members[0].id, bingoItemId = bingoItem.id, title = command.title, subTitle = command.subTitle)
        }

        savedGameBoard.bingoItems.forEachIndexed { index, bingoItem ->
            val command = BingoItemUpdateCommand.createCommand("게임 ${index + 1}", "2023년 ${index + 1}월달까지 게임")
            savedGameBoard.updateBingoItem(memberId = members[0].id, bingoItemId = bingoItem.id, title = command.title, subTitle = command.subTitle)
        }

        savedEnglishBoard.updateBingoMembersPeriod(
            memberId = members[0].id,
            bingoMembers = listOf(members[1].id, members[2].id, members[4].id),
            since = LocalDate.now(),
            until = LocalDate.now().plusDays(7)
        )

        savedHealthBoard.updateBingoMembersPeriod(
            memberId = members[0].id,
            bingoMembers = listOf(members[2].id, members[5].id, members[8].id),
            since = LocalDate.now(),
            until = LocalDate.now().plusDays(7)
        )

        savedGameBoard.updateBingoMembersPeriod(
            memberId = members[0].id,
            bingoMembers = listOf(members[9].id, members[12].id, members[14].id),
            since = LocalDate.now(),
            until = LocalDate.now().plusDays(7)
        )

        englishBoardId = savedEnglishBoard.id
        healthBoardId = savedHealthBoard.id
        gameBoardId = savedGameBoard.id
    }

    test("최근 완료자 memberId 목록 조회") {
        val englishBingoBoard = bingoBoardRepository.findById(englishBoardId).orElseThrow()
        englishBingoBoard.completeBingoItem(englishBingoBoard.bingoItems[0].id, members[0].id)
        englishBingoBoard.completeBingoItem(englishBingoBoard.bingoItems[1].id, members[0].id)

        val healthBingo = bingoBoardRepository.findById(healthBoardId).orElseThrow()
        healthBingo.completeBingoItem(healthBingo.bingoItems[0].id, members[0].id)
        healthBingo.completeBingoItem(healthBingo.bingoItems[1].id, members[0].id)
        healthBingo.completeBingoItem(healthBingo.bingoItems[2].id, healthBingo.bingoMembers[2].memberId)
        healthBingo.completeBingoItem(healthBingo.bingoItems[5].id, healthBingo.bingoMembers[2].memberId)

        val gameBingo = bingoBoardRepository.findById(gameBoardId).orElseThrow()
        gameBingo.completeBingoItem(gameBingo.bingoItems[0].id, members[0].id)
        gameBingo.completeBingoItem(gameBingo.bingoItems[6].id, gameBingo.bingoMembers[1].memberId)

        val completerIds = feedListFindDao.findRecentCompleterMemberIds(emptyList(), isFriend = false)
        completerIds shouldContainExactlyInAnyOrder listOf(
            members[0].id,
            healthBingo.bingoMembers[2].memberId,
            gameBingo.bingoMembers[1].memberId
        )
    }

    test("친구 필터 — 친구의 완료 내역만 조회") {
        val gameBingo = bingoBoardRepository.findById(gameBoardId).orElseThrow()
        gameBingo.completeBingoItem(gameBingo.bingoItems[0].id, members[0].id)
        gameBingo.completeBingoItem(gameBingo.bingoItems[6].id, gameBingo.bingoMembers[1].memberId)

        val completerIds = feedListFindDao.findRecentCompleterMemberIds(
            memberIds = listOf(gameBingo.bingoMembers[1].memberId),
            isFriend = true
        )
        completerIds shouldContainExactlyInAnyOrder listOf(gameBingo.bingoMembers[1].memberId)
    }

    test("완료된 빙고 아이템 projection 조회") {
        val englishBingoBoard = bingoBoardRepository.findById(englishBoardId).orElseThrow()
        englishBingoBoard.completeBingoItem(englishBingoBoard.bingoItems[0].id, members[0].id)
        englishBingoBoard.completeBingoItem(englishBingoBoard.bingoItems[1].id, members[0].id)

        val items = feedListFindDao.findCompletedFeedItems(listOf(members[0].id))
        items.size shouldBe 2
        items.forEach { it.memberId shouldBe members[0].id }
        items.map { it.title } shouldContainExactlyInAnyOrder listOf("영어 1", "영어 2")
    }
})

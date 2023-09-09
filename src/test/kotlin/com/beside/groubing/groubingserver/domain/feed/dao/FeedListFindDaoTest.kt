package com.beside.groubing.groubingserver.domain.feed.dao

import com.beside.groubing.groubingserver.aMember
import com.beside.groubing.groubingserver.config.QuerydslConfig
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardRepository
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoItemUpdateCommand
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.persistence.LocalPersistenceTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.springframework.context.annotation.Import

@LocalPersistenceTest
@Import(QuerydslConfig::class, FeedListFindDao::class)

class FeedListFindDaoTest(
    private val feedListFindDao: FeedListFindDao,

    private val memberRepository: MemberRepository,

    private val bingoBoardRepository: BingoBoardRepository
) : FunSpec({

    var englishBoardId = 1L
    var healthBoardId = 2L
    var gameBoardId = 3L

    var members: List<Member> = listOf()

    beforeEach {
        memberRepository.saveAll(
            (1L..50L).map { aMember(it) }
        )

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

    test("피드 목록 조회") {
        println("In findFeeds")
        println("boardId: $englishBoardId")
        println("boardId: $healthBoardId")
        println("boardId: $gameBoardId")

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
        gameBingo.completeBingoItem(gameBingo.bingoItems[2].id, members[0].id)
        gameBingo.completeBingoItem(gameBingo.bingoItems[3].id, members[0].id)
        gameBingo.completeBingoItem(gameBingo.bingoItems[5].id, members[0].id)
        gameBingo.completeBingoItem(gameBingo.bingoItems[6].id, members[0].id)
        gameBingo.completeBingoItem(gameBingo.bingoItems[6].id, gameBingo.bingoMembers[1].memberId)

        val feeds = feedListFindDao.findFeeds()
        feeds.size shouldBe 3
        feeds[0].feedItems.size shouldBe 5
        feeds[1].feedItems.size shouldBe 2
        feeds[2].feedItems.size shouldBe 1
    }

    test("친구 피드 목록 조회") {
        println("In findFriendFeeds")
        println("boardId: $englishBoardId")
        println("boardId: $healthBoardId")
        println("boardId: $gameBoardId")

        val englishBingo = bingoBoardRepository.findById(englishBoardId).orElseThrow()
        englishBingo.completeBingoItem(englishBingo.bingoItems[2].id, englishBingo.bingoMembers[2].memberId)
        englishBingo.completeBingoItem(englishBingo.bingoItems[5].id, englishBingo.bingoMembers[2].memberId)

        val gameBingo = bingoBoardRepository.findById(gameBoardId).orElseThrow()
        gameBingo.completeBingoItem(gameBingo.bingoItems[0].id, gameBingo.bingoMembers[0].memberId)
        gameBingo.completeBingoItem(gameBingo.bingoItems[2].id, gameBingo.bingoMembers[0].memberId)
        gameBingo.completeBingoItem(gameBingo.bingoItems[3].id, gameBingo.bingoMembers[0].memberId)
        gameBingo.completeBingoItem(gameBingo.bingoItems[5].id, gameBingo.bingoMembers[0].memberId)
        gameBingo.completeBingoItem(gameBingo.bingoItems[6].id, gameBingo.bingoMembers[0].memberId)
        gameBingo.completeBingoItem(gameBingo.bingoItems[6].id, gameBingo.bingoMembers[1].memberId)

        val feeds = feedListFindDao.findFeeds(friendIds = listOf(gameBingo.bingoMembers[0].memberId, gameBingo.bingoMembers[1].memberId, 4L))
        feeds.size shouldBe 2
        feeds[1].feedItems.size shouldBe 1
    }
})

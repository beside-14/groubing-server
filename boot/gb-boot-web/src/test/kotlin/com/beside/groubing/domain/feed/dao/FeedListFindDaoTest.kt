package com.beside.groubing.domain.feed.dao

import com.beside.groubing.aMember
import com.beside.groubing.config.QuerydslConfig
import com.beside.groubing.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.BingoBoardType
import com.beside.groubing.domain.bingo.payload.command.BingoItemUpdateCommand
import com.beside.groubing.domain.bingo.repository.BingoBoardRepositoryAdapter
import com.beside.groubing.domain.member.entity.MemberEntity
import com.beside.groubing.domain.member.repository.MemberJpaRepository
import com.beside.groubing.persistence.LocalPersistenceTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.springframework.context.annotation.Import

@LocalPersistenceTest
@Import(QuerydslConfig::class, FeedListFindDao::class, BingoBoardListFindDao::class, BingoBoardRepositoryAdapter::class)
class FeedListFindDaoTest(
    private val feedListFindDao: FeedListFindDao,

    private val memberRepository: MemberJpaRepository,

    private val bingoBoardAdapter: BingoBoardRepositoryAdapter
) : FunSpec({

    var englishBoardId = 1L
    var healthBoardId = 2L
    var gameBoardId = 3L

    var members: List<MemberEntity> = listOf()

    beforeEach {
        memberRepository.saveAll((1L..50L).map { aMember(it) })
        members = memberRepository.findAll()

        val english = prepareBoard(bingoBoardAdapter, members[0].id, "영어", listOf(members[1].id, members[2].id, members[4].id))
        val health = prepareBoard(bingoBoardAdapter, members[0].id, "운동", listOf(members[2].id, members[5].id, members[8].id))
        val game = prepareBoard(bingoBoardAdapter, members[0].id, "게임", listOf(members[9].id, members[12].id, members[14].id))

        englishBoardId = english.id
        healthBoardId = health.id
        gameBoardId = game.id
    }

    test("최근 완료자 memberId 목록 조회") {
        val englishBingoBoard = bingoBoardAdapter.findOne(englishBoardId)
        englishBingoBoard.completeBingoItem(englishBingoBoard.bingoItems[0].id, members[0].id)
        englishBingoBoard.completeBingoItem(englishBingoBoard.bingoItems[1].id, members[0].id)
        bingoBoardAdapter.update(englishBingoBoard)

        val healthBingo = bingoBoardAdapter.findOne(healthBoardId)
        healthBingo.completeBingoItem(healthBingo.bingoItems[0].id, members[0].id)
        healthBingo.completeBingoItem(healthBingo.bingoItems[1].id, members[0].id)
        healthBingo.completeBingoItem(healthBingo.bingoItems[2].id, healthBingo.bingoMembers[2].memberId)
        healthBingo.completeBingoItem(healthBingo.bingoItems[5].id, healthBingo.bingoMembers[2].memberId)
        bingoBoardAdapter.update(healthBingo)

        val gameBingo = bingoBoardAdapter.findOne(gameBoardId)
        gameBingo.completeBingoItem(gameBingo.bingoItems[0].id, members[0].id)
        gameBingo.completeBingoItem(gameBingo.bingoItems[6].id, gameBingo.bingoMembers[1].memberId)
        bingoBoardAdapter.update(gameBingo)

        val completerIds = feedListFindDao.findRecentCompleterMemberIds(emptyList(), isFriend = false)
        completerIds shouldContainExactlyInAnyOrder listOf(
            members[0].id,
            healthBingo.bingoMembers[2].memberId,
            gameBingo.bingoMembers[1].memberId
        )
    }

    test("친구 필터 — 친구의 완료 내역만 조회") {
        val gameBingo = bingoBoardAdapter.findOne(gameBoardId)
        gameBingo.completeBingoItem(gameBingo.bingoItems[0].id, members[0].id)
        gameBingo.completeBingoItem(gameBingo.bingoItems[6].id, gameBingo.bingoMembers[1].memberId)
        bingoBoardAdapter.update(gameBingo)

        val completerIds = feedListFindDao.findRecentCompleterMemberIds(
            memberIds = listOf(gameBingo.bingoMembers[1].memberId),
            isFriend = true
        )
        completerIds shouldContainExactlyInAnyOrder listOf(gameBingo.bingoMembers[1].memberId)
    }

    test("완료된 빙고 아이템 projection 조회") {
        val englishBingoBoard = bingoBoardAdapter.findOne(englishBoardId)
        englishBingoBoard.completeBingoItem(englishBingoBoard.bingoItems[0].id, members[0].id)
        englishBingoBoard.completeBingoItem(englishBingoBoard.bingoItems[1].id, members[0].id)
        bingoBoardAdapter.update(englishBingoBoard)

        val items = feedListFindDao.findCompletedFeedItems(listOf(members[0].id))
        items.size shouldBe 2
        items.forEach { it.memberId shouldBe members[0].id }
        items.map { it.title } shouldContainExactlyInAnyOrder listOf("영어 1", "영어 2")
    }
})

private fun prepareBoard(
    adapter: BingoBoardRepositoryAdapter,
    ownerId: Long,
    titlePrefix: String,
    memberIds: List<Long>
): BingoBoard {
    val board = BingoBoard.create(ownerId, titlePrefix, 3, BingoBoardType.GROUP, true, 3)
    val saved = adapter.save(board)
    saved.bingoItems.forEachIndexed { index, bingoItem ->
        val command = BingoItemUpdateCommand.of("$titlePrefix ${index + 1}", "2023년 ${index + 1}월달까지 $titlePrefix")
        saved.updateBingoItem(ownerId, bingoItem.id, command.title, command.subTitle)
    }
    saved.updateBingoMembersPeriod(
        memberId = ownerId,
        bingoMembers = memberIds,
        since = LocalDate.now(),
        until = LocalDate.now().plusDays(7)
    )
    return adapter.update(saved)
}

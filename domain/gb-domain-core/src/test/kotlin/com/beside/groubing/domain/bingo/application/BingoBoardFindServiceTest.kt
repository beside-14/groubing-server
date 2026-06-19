package com.beside.groubing.domain.bingo.application

import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import java.time.LocalDateTime

class BingoBoardFindServiceTest : BehaviorSpec({
    val bingoBoardQueryRepository = mockk<BingoBoardQueryRepository>()
    val memberQueryRepository = mockk<MemberQueryRepository>()
    val bingoBoardFindService = BingoBoardFindService(bingoBoardQueryRepository, memberQueryRepository)

    fun aMember(
        id: Long,
        nickname: String,
        active: Boolean,
        deletedAt: LocalDateTime?,
        profileUrl: String?
    ) = Member(
        id = id,
        loginId = "login$id",
        password = "encoded",
        nickname = nickname,
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC,
        fcmToken = null,
        notificationReceive = true,
        active = active,
        deletedAt = deletedAt,
        profileUrl = profileUrl
    )

    Given("그룹 빙고에 탈퇴한 참여자와 활성 참여자가 함께 있을 때") {
        val viewerId = 1L
        val boardId = 10L
        val viewer = aMember(viewerId, "조회자", active = true, deletedAt = null, profileUrl = "p1.png")
        val activeOther = aMember(2L, "활성유저", active = true, deletedAt = null, profileUrl = "p2.png")
        val withdrawnOther = aMember(3L, "탈퇴전닉", active = false, deletedAt = LocalDateTime.now(), profileUrl = "p3.png")

        val bingoBoard = mockk<BingoBoard>()
        every { bingoBoardQueryRepository.findOne(boardId) } returns bingoBoard
        every { bingoBoard.validateViewableBy(viewerId) } just Runs
        every { bingoBoard.otherActiveMemberIdsOf(viewerId) } returns listOf(2L, 3L)
        every { memberQueryRepository.findActiveById(viewerId) } returns viewer
        every { memberQueryRepository.findAll(listOf(2L, 3L)) } returns listOf(activeOther, withdrawnOther)

        When("빙고 상세를 조회하면") {
            val detail = bingoBoardFindService.findOne(viewerId, boardId)

            Then("탈퇴한 참여자는 '탈퇴한 회원'으로 익명화되고 활성 참여자는 그대로 유지된다") {
                val byId = detail.otherMembers.associateBy { it.id }
                byId[2L]!!.nickname shouldBe "활성유저"
                byId[2L]!!.profileUrl shouldBe "p2.png"
                byId[3L]!!.nickname shouldBe "탈퇴한 회원"
                byId[3L]!!.profileUrl shouldBe null
            }
        }
    }
})

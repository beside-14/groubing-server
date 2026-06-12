package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.event.BingoCompleteEvent
import com.beside.groubing.domain.bingo.event.BingoLineCancelEvent
import com.beside.groubing.domain.bingo.event.BingoLineCompleteEvent
import com.beside.groubing.domain.bingo.exception.BingoIllegalStateException
import com.beside.groubing.domain.bingo.exception.BingoInputException
import com.beside.groubing.domain.bingo.fixture.BingoBoardFixture
import com.beside.groubing.domain.bingo.fixture.BingoBoardFixture.LEADER_ID
import com.beside.groubing.domain.bingo.fixture.BingoBoardFixture.PARTICIPANT_ID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import java.time.LocalDate

/**
 * 3x3 보드 기준 itemOrder ↔ 위치 매핑 (Fixture 가 itemOrder 1..9 로 고정):
 *   order 1 2 3   (가로 1번 라인)
 *   order 4 5 6   (가로 2번 라인)
 *   order 7 8 9   (가로 3번 라인)
 * BingoItem.id == itemOrder 로 1:1 매핑되므로 itemId 1,2,3 완료 시 가로 1번 라인 빙고가 성립한다.
 */
class BingoBoardTest : BehaviorSpec({

    Given("신규 보드 생성 시") {
        val memberId = Arb.long().single()
        val bingoSize = Arb.int(3..4).single()
        val board = BingoBoard.create(
            memberId = memberId,
            title = Arb.string().single(),
            goal = Arb.int(1..bingoSize).single(),
            boardType = Arb.enum<BingoBoardType>().single(),
            open = Arb.boolean().single(),
            bingoSize = bingoSize
        )

        When("설정한 빙고 사이즈만큼") {
            val items = board.bingoItems

            Then("빙고 아이템을 생성한다.") {
                items.size shouldBe (board.size * board.size)
            }

            Then("생성자를 리더로 등록한다.") {
                board.isLeader(memberId) shouldBe true
            }

            Then("초기 상태는 임시 빙고(기간 미설정)이며 활성 상태다.") {
                board.period shouldBe null
                board.isStarted() shouldBe false
                board.active shouldBe true
            }
        }
    }

    Given("빙고 아이템 완료(completeBingoItem)") {
        When("아직 임시 빙고(기간 미설정) 상태에서 완료하면") {
            val board = BingoBoardFixture.temporaryBoard(allUpdated = true)

            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    board.completeBingoItem(bingoItemId = 1L, memberId = LEADER_ID)
                }.message shouldBe "아직 임시빙고 상태에서 빙고를 완성할 수 없습니다."
            }
        }

        When("시작된 빙고에서 라인을 완성하지 못하는 아이템 1개만 완료하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)
            board.completeBingoItem(bingoItemId = 1L, memberId = LEADER_ID)

            Then("해당 아이템이 완료 처리된다.") {
                board.bingoItems.findOf(1L).isCompleted(LEADER_ID) shouldBe true
            }

            Then("빙고 라인이 늘지 않았으므로 도메인 이벤트가 발생하지 않는다.") {
                board.pullEvents().shouldHaveSize(0)
            }
        }

        When("가로 한 줄(아이템 1,2,3)을 완료해 빙고 라인이 새로 생기면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))
            board.completeBingoItem(bingoItemId = 1L, memberId = LEADER_ID)
            board.completeBingoItem(bingoItemId = 2L, memberId = LEADER_ID)
            board.completeBingoItem(bingoItemId = 3L, memberId = LEADER_ID)

            Then("BingoLineCompleteEvent 가 1건 발생한다.") {
                val events = board.pullEvents()
                events.shouldHaveSize(1)
                val event = events.single().shouldBeInstanceOf<BingoLineCompleteEvent>()
                event.bingoBoardId shouldBe board.id
                event.bingoBoardTitle shouldBe board.title
                event.totalBingoCount shouldBe 1
                event.memberId shouldBe LEADER_ID
                event.otherMemberIds shouldContainExactlyInAnyOrder listOf(PARTICIPANT_ID)
            }
        }

        When("목표 빙고 수를 이미 달성한 뒤 라인을 늘리지 않는 아이템을 완료하면") {
            // goal=1, 가로 1번 라인(1,2,3) 완료로 이미 1빙고 달성. 그 후 아이템 9(라인 미완성) 완료 →
            // afterBingoCount == beforeBingoCount && isGoal(after) 분기를 탄다.
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))
            board.completeBingoItem(bingoItemId = 1L, memberId = LEADER_ID)
            board.completeBingoItem(bingoItemId = 2L, memberId = LEADER_ID)
            board.completeBingoItem(bingoItemId = 3L, memberId = LEADER_ID)
            board.pullEvents() // 라인 완성 이벤트 비우기
            board.completeBingoItem(bingoItemId = 9L, memberId = LEADER_ID)

            Then("BingoCompleteEvent 가 1건 발생한다.") {
                val events = board.pullEvents()
                events.shouldHaveSize(1)
                val event = events.single().shouldBeInstanceOf<BingoCompleteEvent>()
                event.bingoBoardId shouldBe board.id
                event.bingoBoardTitle shouldBe board.title
                event.memberId shouldBe LEADER_ID
                event.otherMemberIds shouldContainExactlyInAnyOrder listOf(PARTICIPANT_ID)
            }
        }

        When("이미 완료한 아이템을 다시 완료하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)
            board.completeBingoItem(bingoItemId = 1L, memberId = LEADER_ID)

            Then("IllegalStateException 이 발생한다.") {
                shouldThrow<IllegalStateException> {
                    board.completeBingoItem(bingoItemId = 1L, memberId = LEADER_ID)
                }
            }
        }

        When("존재하지 않는 아이템 아이디로 완료하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)

            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    board.completeBingoItem(bingoItemId = 999L, memberId = LEADER_ID)
                }.message shouldBe "입력된 빙고 아이템 아이디가 잘못 되었습니다. id : 999"
            }
        }
    }

    Given("빙고 아이템 완료 취소(cancelBingoItem)") {
        When("아직 임시 빙고 상태에서 취소하면") {
            val board = BingoBoardFixture.temporaryBoard(allUpdated = true)

            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    board.cancelBingoItem(bingoItemId = 1L, memberId = LEADER_ID)
                }.message shouldBe "아직 임시빙고 상태에서 빙고를 취소할 수 없습니다."
            }
        }

        When("완성된 빙고 라인을 깨뜨리는 아이템을 취소하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))
            board.completeBingoItem(bingoItemId = 1L, memberId = LEADER_ID)
            board.completeBingoItem(bingoItemId = 2L, memberId = LEADER_ID)
            board.completeBingoItem(bingoItemId = 3L, memberId = LEADER_ID)
            board.pullEvents() // 완성 이벤트 비우기
            board.cancelBingoItem(bingoItemId = 2L, memberId = LEADER_ID)

            Then("해당 아이템 완료가 해제된다.") {
                board.bingoItems.findOf(2L).isCompleted(LEADER_ID) shouldBe false
            }

            Then("BingoLineCancelEvent 가 1건 발생한다.") {
                val events = board.pullEvents()
                events.shouldHaveSize(1)
                val event = events.single().shouldBeInstanceOf<BingoLineCancelEvent>()
                event.bingoBoardId shouldBe board.id
                event.bingoItemTitle shouldBe board.bingoItems.findOf(2L).title
                event.memberId shouldBe LEADER_ID
                event.otherMemberIds shouldContainExactlyInAnyOrder listOf(PARTICIPANT_ID)
            }
        }

        When("빙고 라인을 깨뜨리지 않는 아이템을 취소하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)
            board.completeBingoItem(bingoItemId = 5L, memberId = LEADER_ID)
            board.pullEvents()
            board.cancelBingoItem(bingoItemId = 5L, memberId = LEADER_ID)

            Then("도메인 이벤트가 발생하지 않는다.") {
                board.pullEvents().shouldHaveSize(0)
            }
        }
    }

    Given("빙고 아이템 셔플(shuffleBingoItems)") {
        When("임시 빙고(아직 시작 전) 상태에서 셔플하면") {
            val board = BingoBoardFixture.temporaryBoard(size = 3, allUpdated = false)
            val beforeImageUrls = board.bingoItems.map { it.imageUrl }.sorted()

            board.shuffleBingoItems()

            Then("아이템 개수와 imageUrl 구성은 보존된다.") {
                board.bingoItems.map { it.imageUrl }.sorted() shouldBe beforeImageUrls
            }

            Then("itemOrder 는 1..N 으로 빠짐없이 재배치된다.") {
                board.bingoItems.map { it.itemOrder }.sorted() shouldBe (1..(board.size * board.size)).toList()
            }
        }

        When("이미 시작된 빙고에서 셔플하면") {
            val board = BingoBoardFixture.startedBoard(size = 3)

            Then("BingoInputException 이 발생한다.") {
                shouldThrow<BingoInputException> {
                    board.shuffleBingoItems()
                }.message shouldBe "임시 빙고가 아니면 shuffle 할 수 없습니다. BingoBoard Id : ${board.id}"
            }
        }
    }

    Given("기본 정보 수정(updateBase)") {
        val newSince = LocalDate.now()
        val newUntil = LocalDate.now().plusDays(30)

        When("리더가 수정하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)
            board.updateBase(memberId = LEADER_ID, title = "변경된 제목", goal = 2, since = newSince, until = newUntil)

            Then("제목/목표/기간이 갱신된다.") {
                board.title shouldBe "변경된 제목"
                board.goal shouldBe 2
                board.since shouldBe newSince
                board.until shouldBe newUntil
            }
        }

        When("리더가 아닌 참여자가 수정하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))

            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    board.updateBase(memberId = PARTICIPANT_ID, title = "x", goal = 1, since = newSince, until = newUntil)
                }.message shouldBe "해당 빙고를 수정할 권한이 없습니다."
            }
        }

        When("빙고에 포함되지 않은 회원이 수정하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)

            Then("BingoInputException 이 발생한다.") {
                shouldThrow<BingoInputException> {
                    board.updateBase(memberId = 999L, title = "x", goal = 1, since = newSince, until = newUntil)
                }
            }
        }
    }

    Given("아이템 내용 수정(updateBingoItem)") {
        When("리더가 아이템 제목/부제목을 수정하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)
            val updated = board.updateBingoItem(memberId = LEADER_ID, bingoItemId = 1L, title = "새 제목", subTitle = "부제목")

            Then("아이템 내용이 갱신되어 반환된다.") {
                updated.title shouldBe "새 제목"
                updated.subTitle shouldBe "부제목"
                board.bingoItems.findOf(1L).title shouldBe "새 제목"
            }
        }

        When("리더가 아닌 회원이 아이템을 수정하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))

            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    board.updateBingoItem(memberId = PARTICIPANT_ID, bingoItemId = 1L, title = "x", subTitle = null)
                }
            }
        }
    }

    Given("멤버/기간 수정(updateBingoMembersPeriod)") {
        val since = LocalDate.now()
        val until = LocalDate.now().plusDays(10)

        When("리더가 새 멤버와 기간을 등록하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)
            board.updateBingoMembersPeriod(memberId = LEADER_ID, bingoMembers = listOf(3L, 4L), since = since, until = until)

            Then("기간이 갱신되고 새 멤버가 추가된다.") {
                board.since shouldBe since
                board.until shouldBe until
                board.bingoMembers.memberIds() shouldContainExactlyInAnyOrder listOf(LEADER_ID, 3L, 4L)
            }
        }

        When("리더가 아닌 회원이 멤버/기간을 수정하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))

            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    board.updateBingoMembersPeriod(memberId = PARTICIPANT_ID, bingoMembers = listOf(3L), since = since, until = until)
                }
            }
        }
    }

    Given("메모 수정(updateBingoMemo)") {
        When("리더가 메모를 수정하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)
            board.updateBingoMemo(memberId = LEADER_ID, memo = "오늘의 메모")

            Then("메모가 갱신된다.") {
                board.memo shouldBe "오늘의 메모"
            }
        }

        When("리더가 아닌 회원이 메모를 수정하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))

            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    board.updateBingoMemo(memberId = PARTICIPANT_ID, memo = "x")
                }
            }
        }
    }

    Given("공개 여부 수정(updateBingoOpen)") {
        When("리더가 공개 여부를 변경하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, open = true)
            board.updateBingoOpen(memberId = LEADER_ID, open = false)

            Then("공개 여부가 갱신된다.") {
                board.open shouldBe false
            }
        }

        When("리더가 아닌 회원이 공개 여부를 변경하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))

            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    board.updateBingoOpen(memberId = PARTICIPANT_ID, open = false)
                }
            }
        }
    }

    Given("빙고 삭제(delete)") {
        When("삭제하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)
            board.delete()

            Then("비활성 상태가 된다.") {
                board.active shouldBe false
            }
        }
    }

    Given("빙고 나가기 검증(validateCanLeave)") {
        When("리더가 나가려 하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))

            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    board.validateCanLeave(LEADER_ID)
                }.message shouldBe "그룹 빙고 리더는 빙고를 나갈 수 없습니다."
            }
        }

        When("참여자가 나가려 하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))

            Then("예외가 발생하지 않는다.") {
                board.validateCanLeave(PARTICIPANT_ID)
            }
        }
    }

    Given("멤버 탈퇴 처리(inactiveByMemberId)") {
        When("완료 기록이 있는 참여자가 탈퇴하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))
            board.completeBingoItem(bingoItemId = 1L, memberId = PARTICIPANT_ID)
            board.inactiveByMemberId(PARTICIPANT_ID)

            Then("해당 회원은 활성 다른 멤버 목록에서 제외된다.") {
                board.otherActiveMemberIdsOf(LEADER_ID).shouldHaveSize(0)
            }

            Then("해당 회원의 완료 기록도 비활성화된다.") {
                val completeMember = board.bingoItems.findOf(1L).getBingoCompleteMember(PARTICIPANT_ID)
                completeMember!!.active shouldBe false
            }
        }
    }

    Given("권한/리더 조회") {
        val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))

        When("리더에 대해 isLeader 를 호출하면") {
            Then("true 를 반환한다.") {
                board.isLeader(LEADER_ID) shouldBe true
            }
        }

        When("참여자에 대해 isLeader 를 호출하면") {
            Then("false 를 반환한다.") {
                board.isLeader(PARTICIPANT_ID) shouldBe false
            }
        }

        When("참여자가 validateAuthority 를 호출하면") {
            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    board.validateAuthority(PARTICIPANT_ID)
                }
            }
        }
    }

    Given("열람 권한 검증(validateViewableBy)") {
        When("리더가 임시 빙고를 열람하면") {
            val board = BingoBoardFixture.temporaryBoard(participantIds = listOf(PARTICIPANT_ID))

            Then("예외가 발생하지 않는다.") {
                board.validateViewableBy(LEADER_ID)
            }
        }

        When("참여자가 아직 시작되지 않은 임시 빙고를 열람하면") {
            val board = BingoBoardFixture.temporaryBoard(participantIds = listOf(PARTICIPANT_ID))

            Then("BingoInputException 이 발생한다.") {
                shouldThrow<BingoInputException> {
                    board.validateViewableBy(PARTICIPANT_ID)
                }.message shouldBe "접근할 수 없는 빙고보드입니다."
            }
        }

        When("참여자가 이미 시작된 빙고를 열람하면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1, participantIds = listOf(PARTICIPANT_ID))

            Then("예외가 발생하지 않는다.") {
                board.validateViewableBy(PARTICIPANT_ID)
            }
        }
    }

    Given("남은 기간 계산(calculateLeftDays)") {
        When("종료일이 이미 지난 빙고이면") {
            val board = BingoBoardFixture.startedBoard(size = 3, goal = 1)
            // 종료일을 과거로 만들기 위해 of 로 직접 만료 기간을 주입한다.
            val expired = BingoBoard.of(
                id = 1L,
                title = board.title,
                boardType = BingoBoardType.GROUP,
                bingoColor = BingoColor.BLUE,
                open = true,
                memo = null,
                active = true,
                bingoSize = BingoSize.cache(3),
                bingoGoal = BingoGoal.create(1, BingoSize.cache(3)),
                period = BingoPeriod.of(
                    LocalDate.now().minusDays(10),
                    LocalDate.now().minusDays(1)
                ),
                bingoMembers = BingoBoardFixture.bingoMembers(),
                bingoItems = BingoBoardFixture.bingoItems(3, updated = true)
            )

            Then("음수가 아닌 0 으로 보정된다.") {
                expired.calculateLeftDays() shouldBe 0L
            }

            Then("isFinished 는 true 다.") {
                expired.isFinished() shouldBe true
            }
        }

        When("기간이 설정되지 않은 임시 빙고이면") {
            val board = BingoBoardFixture.temporaryBoard()

            Then("남은 기간은 0 이고 isFinished 는 false 다.") {
                board.calculateLeftDays() shouldBe 0L
                board.isFinished() shouldBe false
            }
        }
    }
})

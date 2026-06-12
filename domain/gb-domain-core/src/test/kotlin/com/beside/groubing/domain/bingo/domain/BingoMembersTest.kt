package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.exception.BingoIllegalStateException
import com.beside.groubing.domain.bingo.exception.BingoInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class BingoMembersTest : BehaviorSpec({

    val leaderId = 1L
    val participantId = 2L
    val bingoBoardId = 10L

    fun membersOf(leader: Long = leaderId, participants: List<Long> = listOf(participantId)): BingoMembers {
        val list = mutableListOf(
            BingoMember.of(1L, leader, BingoMemberType.LEADER, active = true)
        )
        participants.forEachIndexed { index, memberId ->
            list.add(BingoMember.of((index + 2).toLong(), memberId, BingoMemberType.PARTICIPANT, active = true))
        }
        return BingoMembers.of(list)
    }

    Given("BingoMembers.ofLeader") {
        When("리더만으로 생성하면") {
            val members = BingoMembers.ofLeader(leaderId)

            Then("리더 1명이 LEADER 타입으로 등록된다.") {
                members.memberIds() shouldBe listOf(leaderId)
                members.isLeaderOf(leaderId) shouldBe true
            }
        }
    }

    Given("isLeaderOf") {
        val members = membersOf()

        When("리더 아이디로 확인하면") {
            Then("true 를 반환한다.") {
                members.isLeaderOf(leaderId) shouldBe true
            }
        }

        When("참여자 아이디로 확인하면") {
            Then("false 를 반환한다.") {
                members.isLeaderOf(participantId) shouldBe false
            }
        }
    }

    Given("findOf") {
        val members = membersOf()

        When("포함된 회원으로 조회하면") {
            Then("해당 멤버를 반환한다.") {
                members.findOf(participantId, bingoBoardId).memberId shouldBe participantId
            }
        }

        When("포함되지 않은 회원으로 조회하면") {
            Then("BingoInputException 이 발생한다.") {
                shouldThrow<BingoInputException> {
                    members.findOf(999L, bingoBoardId)
                }
            }
        }
    }

    Given("validateLeaderOf") {
        val members = membersOf()

        When("리더가 검증하면") {
            Then("예외가 발생하지 않는다.") {
                members.validateLeaderOf(leaderId, bingoBoardId)
            }
        }

        When("참여자가 검증하면") {
            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    members.validateLeaderOf(participantId, bingoBoardId)
                }.message shouldBe "해당 빙고를 수정할 권한이 없습니다."
            }
        }

        When("포함되지 않은 회원이 검증하면") {
            Then("BingoInputException 이 발생한다.") {
                shouldThrow<BingoInputException> {
                    members.validateLeaderOf(999L, bingoBoardId)
                }
            }
        }
    }

    Given("addNewMembers") {
        When("새 회원들을 추가하면") {
            val members = membersOf(participants = emptyList())
            members.addNewMembers(listOf(2L, 3L))

            Then("PARTICIPANT 로 추가되어 전체 회원 목록에 반영된다.") {
                members.memberIds() shouldContainExactlyInAnyOrder listOf(leaderId, 2L, 3L)
                members.isLeaderOf(2L) shouldBe false
            }
        }
    }

    Given("otherMemberIdsOf / otherActiveMemberIdsOf") {
        When("특정 회원을 제외하고 조회하면") {
            val members = membersOf(participants = listOf(2L, 3L))

            Then("나머지 회원 아이디를 반환한다.") {
                members.otherMemberIdsOf(leaderId) shouldContainExactlyInAnyOrder listOf(2L, 3L)
            }
        }

        When("비활성 회원이 섞여 있고 활성 회원만 조회하면") {
            val members = membersOf(participants = listOf(2L, 3L))
            members.inactivateOf(2L, bingoBoardId)

            Then("비활성 회원은 제외된다.") {
                members.otherActiveMemberIdsOf(leaderId) shouldContainExactlyInAnyOrder listOf(3L)
            }
        }
    }

    Given("inactivateOf") {
        When("포함된 회원을 비활성화하면") {
            val members = membersOf()
            members.inactivateOf(participantId, bingoBoardId)

            Then("해당 회원이 활성 목록에서 제외된다.") {
                members.otherActiveMemberIdsOf(leaderId).contains(participantId) shouldBe false
            }
        }

        When("포함되지 않은 회원을 비활성화하면") {
            val members = membersOf()

            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    members.inactivateOf(999L, bingoBoardId)
                }
            }
        }
    }
})

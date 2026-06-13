package com.beside.groubing.domain.bingo.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class BingoItemTest : BehaviorSpec({

    val memberId = 1L

    fun newItem(): BingoItem = BingoItem.create(itemOrder = 1, imageUrl = "g")

    Given("BingoItem.create") {
        When("itemOrder 와 imageUrl 로 생성하면") {
            val item = newItem()

            Then("title 이 비어있어 isUpdated 가 false 다.") {
                item.title shouldBe null
                item.isUpdated() shouldBe false
            }

            Then("완료한 멤버가 없다.") {
                item.completeMembers.shouldHaveSize(0)
            }
        }
    }

    Given("complete") {
        When("완료한 적 없는 회원이 완료하면") {
            val item = newItem()
            item.complete(memberId)

            Then("해당 회원이 완료 멤버로 등록된다.") {
                item.isCompleted(memberId) shouldBe true
                item.completeMembers.shouldHaveSize(1)
            }
        }

        When("이미 완료한 회원이 다시 완료하면") {
            val item = newItem()
            item.complete(memberId)

            Then("IllegalStateException 이 발생한다.") {
                shouldThrow<IllegalStateException> {
                    item.complete(memberId)
                }
            }
        }

        When("서로 다른 회원이 각각 완료하면") {
            val item = newItem()
            item.complete(1L)
            item.complete(2L)

            Then("두 회원 모두 완료 상태로 등록된다.") {
                item.isCompleted(1L) shouldBe true
                item.isCompleted(2L) shouldBe true
                item.completeMembers.shouldHaveSize(2)
            }
        }
    }

    Given("cancel") {
        When("완료한 회원이 취소하면") {
            val item = newItem()
            item.complete(memberId)
            item.cancel(memberId)

            Then("완료 상태가 해제된다.") {
                item.isCompleted(memberId) shouldBe false
                item.completeMembers.shouldHaveSize(0)
            }
        }

        When("완료한 적 없는 회원이 취소하면") {
            val item = newItem()
            item.cancel(memberId)

            Then("아무 변화 없이 완료 멤버가 비어있다.") {
                item.completeMembers.shouldHaveSize(0)
            }
        }
    }

    Given("update") {
        When("title 과 subTitle 을 수정하면") {
            val item = newItem()
            item.update(title = "제목", subTitle = "부제목")

            Then("내용이 갱신되고 isUpdated 가 true 가 된다.") {
                item.title shouldBe "제목"
                item.subTitle shouldBe "부제목"
                item.isUpdated() shouldBe true
            }
        }
    }

    Given("changeItemOrder") {
        When("itemOrder 를 변경하면") {
            val item = newItem()
            item.changeItemOrder(5)

            Then("순서가 갱신된다.") {
                item.itemOrder shouldBe 5
            }
        }
    }

    Given("getImageUrl") {
        When("완료하지 않은 회원에 대해 호출하면") {
            val item = newItem()

            Then("기본 이미지 경로를 반환한다.") {
                item.getImageUrl(memberId) shouldBe "g${BingoItem.EXTENSION}"
            }
        }

        When("완료한 회원에 대해 호출하면") {
            val item = newItem()
            item.complete(memberId)

            Then("완료 이미지 경로를 반환한다.") {
                item.getImageUrl(memberId) shouldBe "g_complete${BingoItem.EXTENSION}"
            }
        }
    }

    Given("getBingoCompleteMember") {
        When("완료한 회원으로 조회하면") {
            val item = newItem()
            item.complete(memberId)

            Then("해당 완료 멤버를 반환한다.") {
                item.getBingoCompleteMember(memberId)!!.memberId shouldBe memberId
            }
        }

        When("완료하지 않은 회원으로 조회하면") {
            val item = newItem()

            Then("null 을 반환한다.") {
                item.getBingoCompleteMember(memberId) shouldBe null
            }
        }
    }
})

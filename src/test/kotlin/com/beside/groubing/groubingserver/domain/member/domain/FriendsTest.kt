package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.aMember
import com.beside.groubing.groubingserver.domain.friendship.domain.FriendshipStatus
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

class FriendsTest : ExpectSpec({
    context("친구 관련 기능 테스트") {
        val member = aMember()
        val friends = (1..10).map { id -> aMember(id.toLong()) }
        val friendships = friends.map { friend -> member.addFriend(friend) }

        expect("요청을 수락한 친구가 없어 0명의 친구가 존재한다.") {
            member.friends.members.size shouldBe 0
        }

        expect("아이디가 홀수인 회원의 친구 요청만 수락한다.") {
            friendships.forEach { friendship -> friendship.status = FriendshipStatus.ACCEPT }
            member.friends.members.size shouldBe 10
        }

        expect("등록된 친구 중 한명을 삭제한다.") {
            friendships.forEach { friendship -> friendship.status = FriendshipStatus.ACCEPT }
            member.deleteFriend(friends.first())

            member.friends.members.size shouldBe 9
        }

        expect("친구 목록에서 닉네임으로 찾는다.") {
            friendships.forEach { friendship -> friendship.status = FriendshipStatus.ACCEPT }
            val lastFriend = friends.last()
            val findFriends = member.friends.findByNickname(lastFriend.nickname)

            findFriends.size shouldBe 1
            findFriends.first().nickname shouldBe lastFriend.nickname
        }
    }
})

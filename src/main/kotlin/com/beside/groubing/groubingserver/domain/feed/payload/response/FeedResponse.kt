package com.beside.groubing.groubingserver.domain.feed.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem
import com.beside.groubing.groubingserver.domain.member.domain.Member

class FeedResponse private constructor(
    val memberId: Long,

    val nickname: String,

    val profile: String?,

    val feedItems: List<FeedItemDto>
)
{
    class FeedItemDto private constructor(
        val title: String
    ) {
        companion object {
            fun create(bingoItem: BingoItem): FeedItemDto {
                return FeedItemDto(bingoItem.title ?: throw IllegalStateException("feed item은 bingoItem.title이 null일 수 없습니다. bingoItem id : ${bingoItem.id}"))
            }
        }
    }

    companion object {
        fun create(member: Member, bingoItems: List<BingoItem>): FeedResponse {
            return FeedResponse(memberId = member.id, nickname = member.nickname, profile = member.profile?.url,
                feedItems = bingoItems.map { FeedItemDto.create(it) }
            )
        }
    }
}

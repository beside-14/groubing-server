package com.beside.groubing.groubingserver.domain.feed.api

import com.beside.groubing.groubingserver.domain.feed.application.FeedListFindService
import com.beside.groubing.groubingserver.domain.feed.application.FriendFeedListFindService
import com.beside.groubing.groubingserver.domain.feed.payload.response.FeedResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class FeedListFindApi(
    private val feedListFindService: FeedListFindService,

    private val friendFeedListFindService: FriendFeedListFindService
) {
    @GetMapping("/feeds")
    fun findFeeds(): ApiResponse<List<FeedResponse>> {
        return ApiResponse.OK(feedListFindService.findAllFeeds())
    }

    @GetMapping("/friend-feeds")
    fun findFriendFeeds(@AuthenticationPrincipal memberId: Long): ApiResponse<List<FeedResponse>> {
        return ApiResponse.OK(friendFeedListFindService.findFriendFeeds(memberId))
    }
}

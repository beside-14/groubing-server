package com.beside.groubing.domain.friend.application

import com.beside.groubing.domain.friend.domain.Friend
import com.beside.groubing.domain.friend.domain.FriendAddValidator
import com.beside.groubing.domain.friend.domain.FriendRelations
import com.beside.groubing.domain.friend.domain.port.FriendCommandRepository
import com.beside.groubing.domain.friend.domain.port.FriendQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendAddService(
    private val friendQueryRepository: FriendQueryRepository,
    private val friendCommandRepository: FriendCommandRepository,
    private val friendAddValidator: FriendAddValidator
) {
    fun add(inviterId: Long, inviteeId: Long) {
        friendAddValidator.validate(inviterId, inviteeId)

        val friends = friendQueryRepository.findAllBetween(inviterId, inviteeId)
        if (friends.isEmpty()) {
            friendCommandRepository.save(Friend.create(inviterId, inviteeId))
            return
        }

        val rependable = FriendRelations(friends).findRependable(inviterId, inviteeId)
        friendCommandRepository.update(rependable.repend())
    }
}

package com.beside.groubing.domain.friend.repository

import com.beside.groubing.domain.friend.dao.FriendFindDao
import com.beside.groubing.domain.friend.dao.FriendMemberInfo
import com.beside.groubing.domain.friend.domain.Friend
import com.beside.groubing.domain.friend.domain.FriendMember
import com.beside.groubing.domain.friend.domain.port.FriendCommandRepository
import com.beside.groubing.domain.friend.domain.port.FriendQueryRepository
import com.beside.groubing.domain.friend.entity.FriendEntity
import com.beside.groubing.domain.friend.exception.FriendInputException
import org.springframework.stereotype.Repository

@Repository
class FriendRepositoryAdapter(
    private val friendJpaRepository: FriendJpaRepository,
    private val friendFindDao: FriendFindDao
) : FriendCommandRepository, FriendQueryRepository {
    override fun save(friend: Friend): Friend {
        return friendJpaRepository.save(FriendEntity.from(friend)).toDomain()
    }

    override fun update(friend: Friend): Friend {
        val entity = findEntityById(friend.id)
        entity.applyStatus(friend)
        return entity.toDomain()
    }

    override fun deleteAllBetween(memberIdA: Long, memberIdB: Long) {
        val entities = friendJpaRepository.findAllBetween(memberIdA, memberIdB)
        if (entities.isNotEmpty()) friendJpaRepository.deleteAll(entities)
    }

    override fun findOne(id: Long): Friend {
        return findEntityById(id).toDomain()
    }

    override fun findAllBetween(memberIdA: Long, memberIdB: Long): List<Friend> {
        return friendJpaRepository.findAllBetween(memberIdA, memberIdB).map { it.toDomain() }
    }

    override fun findAllAcceptedOf(memberId: Long): List<FriendMember> =
        friendFindDao.findAllAcceptedOf(memberId).map(::toFriendMember)

    override fun findAllReceivedBy(inviteeId: Long): List<FriendMember> =
        friendFindDao.findAllReceivedBy(inviteeId).map(::toFriendMember)

    override fun findAllSentBy(inviterId: Long): List<FriendMember> =
        friendFindDao.findAllSentBy(inviterId).map(::toFriendMember)

    private fun toFriendMember(info: FriendMemberInfo): FriendMember = FriendMember(
        friendId = info.friendId,
        memberId = info.memberId,
        email = info.email,
        nickname = info.nickname,
        profileFileName = info.profileFileName,
        status = info.status
    )

    private fun findEntityById(id: Long): FriendEntity {
        return friendJpaRepository.findById(id).orElseThrow {
            FriendInputException("존재하지 않는 친구 요청입니다.")
        }
    }
}

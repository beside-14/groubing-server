package com.beside.groubing.groubingserver.domain.friend.repository

import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendCommandRepository
import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendQueryRepository
import com.beside.groubing.groubingserver.domain.friend.entity.FriendEntity
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import org.springframework.stereotype.Repository

@Repository
class FriendRepositoryAdapter(
    private val friendJpaRepository: FriendJpaRepository
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

    private fun findEntityById(id: Long): FriendEntity {
        return friendJpaRepository.findById(id).orElseThrow {
            FriendInputException("존재하지 않는 친구 요청입니다.")
        }
    }
}

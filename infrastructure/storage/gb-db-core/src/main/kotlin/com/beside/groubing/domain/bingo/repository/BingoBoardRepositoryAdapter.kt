package com.beside.groubing.domain.bingo.repository

import com.beside.groubing.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.domain.bingo.domain.BingoBoard
import com.beside.groubing.domain.bingo.domain.BingoCompleteMember
import com.beside.groubing.domain.bingo.domain.BingoItems
import com.beside.groubing.domain.bingo.domain.BingoMembers
import com.beside.groubing.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.bingo.entity.BingoBoardEntity
import com.beside.groubing.domain.bingo.entity.BingoCompleteMemberEntity
import com.beside.groubing.domain.bingo.entity.BingoItemEntity
import com.beside.groubing.domain.bingo.entity.BingoMemberEntity
import com.beside.groubing.domain.bingo.exception.BingoInputException
import org.springframework.stereotype.Repository

@Repository
class BingoBoardRepositoryAdapter(
    private val bingoBoardJpaRepository: BingoBoardJpaRepository,
    private val bingoBoardListFindDao: BingoBoardListFindDao
) : BingoBoardCommandRepository, BingoBoardQueryRepository {

    override fun save(bingoBoard: BingoBoard): BingoBoard {
        val entity = BingoBoardEntity.from(bingoBoard)
        bingoBoard.pullEvents().forEach { entity.publishEvent(it) }
        return bingoBoardJpaRepository.save(entity).toDomain()
    }

    override fun update(bingoBoard: BingoBoard): BingoBoard {
        val entity = findActiveEntityById(bingoBoard.id)
        entity.applyChanges(bingoBoard)
        syncBingoMembers(entity.bingoMembers, bingoBoard.bingoMembers)
        syncBingoItems(entity.bingoItems, bingoBoard.bingoItems)
        bingoBoard.pullEvents().forEach { entity.publishEvent(it) }
        return entity.toDomain()
    }

    override fun inactivateAllOf(memberId: Long) {
        bingoBoardListFindDao.find(memberId)
            .forEach { entity ->
                val domain = entity.toDomain()
                domain.inactiveByMemberId(memberId)
                entity.applyChanges(domain)
                syncBingoMembers(entity.bingoMembers, domain.bingoMembers)
                syncBingoItems(entity.bingoItems, domain.bingoItems)
            }
    }

    override fun findOne(id: Long): BingoBoard = findActiveEntityById(id).toDomain()

    override fun findAllOf(memberId: Long): List<BingoBoard> =
        bingoBoardListFindDao.find(memberId).map { it.toDomain() }

    override fun findAllIdsOf(memberId: Long): List<Long> =
        bingoBoardListFindDao.findAllIdsOf(memberId)

    override fun isLeaderOf(bingoBoardId: Long, memberId: Long): Boolean =
        bingoBoardListFindDao.isLeaderOf(bingoBoardId, memberId)

    private fun findActiveEntityById(id: Long): BingoBoardEntity {
        return bingoBoardJpaRepository.findByIdAndActiveIsTrue(id)
            .orElseThrow { BingoInputException("존재하지 않는 BingoBoard Id입니다. : $id") }
    }

    private fun syncBingoMembers(entities: MutableList<BingoMemberEntity>, domains: BingoMembers) {
        val existingById = entities.filter { it.id != 0L }.associateBy { it.id }
        entities.removeAll { it.id != 0L && domains.none { d -> d.id == it.id } }
        domains.forEach { domain ->
            if (domain.id == 0L) {
                entities.add(BingoMemberEntity.from(domain))
            } else {
                existingById[domain.id]?.applyChanges(domain)
            }
        }
    }

    private fun syncBingoItems(entities: MutableList<BingoItemEntity>, domains: BingoItems) {
        val existingById = entities.associateBy { it.id }
        domains.forEach { domain ->
            val entity = existingById[domain.id]
                ?: throw IllegalStateException("BingoItem id ${domain.id} 에 대응하는 entity 가 없습니다.")
            entity.applyChanges(domain)
            syncCompleteMembers(entity.completeMembers, domain.completeMembers)
        }
    }

    private fun syncCompleteMembers(entities: MutableSet<BingoCompleteMemberEntity>, domains: Set<BingoCompleteMember>) {
        val existingById = entities.filter { it.id != 0L }.associateBy { it.id }
        entities.removeAll { it.id != 0L && domains.none { d -> d.id == it.id } }
        domains.forEach { domain ->
            if (domain.id == 0L) {
                entities.add(BingoCompleteMemberEntity.from(domain))
            } else {
                existingById[domain.id]?.applyChanges(domain)
            }
        }
    }
}

package com.beside.groubing.groubingserver.domain.blocked.dao

import com.beside.groubing.groubingserver.domain.blocked.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.blocked.domain.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.blocked.domain.QBlockedMember.blockedMember
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BlockedMemberFindDao(
    private val queryFactory: JPAQueryFactory,
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun findByRequesterOrTargetMember(requesterId: Long, targetMemberId: Long): MutableList<BlockedMember>? {
        return queryFactory.selectFrom(blockedMember)
            .where(
                blockedMember.requester.id.eq(requesterId)
                    .and(blockedMember.targetMember.id.eq(targetMemberId))
                    .or(
                        blockedMember.requester.id.eq(targetMemberId)
                            .and(blockedMember.targetMember.id.eq(requesterId))
                    )
            ).fetch()
    }

    fun findById(id: Long): BlockedMember {
        return blockedMemberRepository.findById(id).orElseThrow { FriendInputException("차단 내역이 존재하지 않습니다.") }
    }

    fun findAllById(
        id: Long,
        predicateFunc: ((BooleanBuilder) -> BooleanBuilder)? = null
    ): List<Member> {
        val query = queryFactory.from(blockedMember)
            .innerJoin(blockedMember.targetMember).fetchJoin()
        var predicate = BooleanBuilder()
            .and(blockedMember.requester.id.eq(id))

        takeIf { predicateFunc != null }?.apply { predicate = predicateFunc!!.invoke(predicate) }

        return query.select(blockedMember)
            .where(predicate)
            .orderBy(blockedMember.createdDate.desc())
            .fetch()
            .map { blockedMember -> blockedMember.targetMember }
    }
}

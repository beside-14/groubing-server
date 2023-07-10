package com.beside.groubing.groubingserver.domain.member.dao

import com.beside.groubing.groubingserver.domain.member.domain.QBlockedMember.blockedMember
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BlockedMemberExistDao(
    private val queryFactory: JPAQueryFactory
) {
    fun existByRequesterOrTargetMember(requesterId: Long, targetMemberId: Long): Boolean {
        val result = queryFactory.select(blockedMember.count())
            .from(blockedMember)
            .where(
                blockedMember.requester.id.eq(requesterId).and(blockedMember.targetMember.id.eq(targetMemberId)).or(
                    blockedMember.requester.id.eq(targetMemberId).and(blockedMember.targetMember.id.eq(requesterId))
                )
            )
            .fetchOne()

        return result != null && result > 0
    }
}

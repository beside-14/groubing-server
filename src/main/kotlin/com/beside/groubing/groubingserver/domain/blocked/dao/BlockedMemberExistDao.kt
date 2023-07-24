package com.beside.groubing.groubingserver.domain.blocked.dao

import com.beside.groubing.groubingserver.domain.blocked.domain.QBlockedMember.blockedMember
import com.beside.groubing.groubingserver.domain.blocked.exception.BlockedMemberInputException
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BlockedMemberExistDao(
    private val queryFactory: JPAQueryFactory
) {
    fun existByRequesterOrTargetMember(requesterId: Long, targetMemberId: Long) {
        val result = queryFactory.select(blockedMember.count())
            .from(blockedMember)
            .where(
                blockedMember.requester.id.eq(requesterId).and(blockedMember.targetMember.id.eq(targetMemberId)).or(
                    blockedMember.requester.id.eq(targetMemberId).and(blockedMember.targetMember.id.eq(requesterId))
                )
            )
            .fetchOne()

        if (result != null && result > 0) throw BlockedMemberInputException("차단된 유저입니다.")
    }
}

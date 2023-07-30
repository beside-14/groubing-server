package com.beside.groubing.groubingserver.domain.blockedmember.dao

import com.beside.groubing.groubingserver.domain.blockedmember.domain.QBlockedMember.blockedMember
import com.beside.groubing.groubingserver.domain.blockedmember.exception.BlockedMemberInputException
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BlockedMemberValidateDao(
    private val queryFactory: JPAQueryFactory
) {
    fun validateEachOther(requesterId: Long, targetMemberId: Long) {
        val result = queryFactory
            .selectOne()
            .from(blockedMember)
            .where(
                blockedMember.requester.id.eq(requesterId).and(blockedMember.targetMember.id.eq(targetMemberId)).or(
                    blockedMember.requester.id.eq(targetMemberId).and(blockedMember.targetMember.id.eq(requesterId))
                )
            )
            .fetchFirst()

        if (result != null && result > 0) throw BlockedMemberInputException("차단된 유저입니다.")
    }

    fun validateBlockMember(requesterId: Long, targetMemberId: Long) {
        val result = queryFactory
            .selectOne()
            .from(blockedMember)
            .where(blockedMember.requester.id.eq(requesterId).and(blockedMember.targetMember.id.eq(targetMemberId)))
            .fetchFirst()

        if (result != null && result > 0) throw BlockedMemberInputException("차단된 유저입니다.")
    }
}

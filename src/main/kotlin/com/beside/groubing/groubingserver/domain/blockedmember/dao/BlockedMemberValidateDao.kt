package com.beside.groubing.groubingserver.domain.blockedmember.dao

import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.blockedmember.domain.QBlockedMember.blockedMember
import com.beside.groubing.groubingserver.domain.blockedmember.exception.BlockedMemberInputException
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BlockedMemberValidateDao(
    private val queryFactory: JPAQueryFactory
) {
    private fun isBlockedPredicate(requesterId: Long, targetMemberId: Long): BooleanExpression {
        return blockedMember.requester.id.eq(requesterId).and(blockedMember.targetMember.id.eq(targetMemberId))
    }

    private fun validate(predicate: BooleanExpression, message: String) {
        val result = queryFactory
            .selectOne()
            .from(blockedMember)
            .where(predicate)
            .fetchFirst()
        if (result != null && result > 0) throw BlockedMemberInputException(message)
    }

    fun validateEachOther(requesterId: Long, targetMemberId: Long) {
        validate(
            isBlockedPredicate(requesterId, targetMemberId)
                .or(isBlockedPredicate(targetMemberId, requesterId)), "내가 이미 차단했거나 상대방이 나를 차단했습니다."
        )
    }

    fun validateBlockMember(requesterId: Long, targetMemberId: Long) {
        validate(isBlockedPredicate(requesterId, targetMemberId), "이미 차단한 유저입니다.")
    }

    fun validateUnblock(requesterId: Long, blockedMember: BlockedMember) {
        if (!blockedMember.isBlockedMember(requesterId)) {
            throw BlockedMemberInputException("차단을 해제할 권한이 없습니다.")
        }
    }
}

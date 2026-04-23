package com.beside.groubing.groubingserver.domain.blockedmember.dao

import com.beside.groubing.groubingserver.domain.blockedmember.entity.QBlockedMemberEntity.blockedMemberEntity
import com.beside.groubing.groubingserver.domain.member.entity.QMemberEntity.memberEntity as member
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BlockedMemberFindDao(
    private val queryFactory: JPAQueryFactory
) {
    fun findAllRequestedBy(requesterId: Long): List<BlockedMemberTargetInfo> {
        return queryFactory.select(
            QBlockedMemberTargetInfo(
                member.id,
                member.email,
                member.nickname,
                member.profile.fileName
            )
        )
            .from(blockedMemberEntity)
            .innerJoin(member).on(blockedMemberEntity.targetMemberId.eq(member.id))
            .leftJoin(member.profile)
            .where(blockedMemberEntity.requesterId.eq(requesterId).and(member.active.isTrue))
            .orderBy(blockedMemberEntity.createdDate.desc())
            .fetch()
    }
}

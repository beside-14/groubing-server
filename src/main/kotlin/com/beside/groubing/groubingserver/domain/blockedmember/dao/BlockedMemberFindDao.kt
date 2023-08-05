package com.beside.groubing.groubingserver.domain.blockedmember.dao

import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.blockedmember.domain.QBlockedMember.blockedMember
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BlockedMemberFindDao(
    private val queryFactory: JPAQueryFactory,
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun findById(id: Long): BlockedMember {
        return blockedMemberRepository.findById(id).orElseThrow { FriendInputException("차단 내역이 존재하지 않습니다.") }
    }

    fun findAllById(id: Long): List<Member> {
        return queryFactory.selectFrom(blockedMember)
            .innerJoin(blockedMember.targetMember)
            .where(blockedMember.requester.id.eq(id))
            .orderBy(blockedMember.createdDate.desc())
            .fetch()
            .map { blockedMember -> blockedMember.targetMember }
    }
}

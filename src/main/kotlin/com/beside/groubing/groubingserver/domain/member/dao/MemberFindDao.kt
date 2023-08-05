package com.beside.groubing.groubingserver.domain.member.dao

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberMap
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import org.springframework.stereotype.Repository

@Repository
class MemberFindDao(
    private val memberRepository: MemberRepository
) {
    fun findAllById(ids: List<Long>): MemberMap {
        return MemberMap(memberRepository.findAllById(ids))
    }

    fun findExistingMemberById(id: Long): Member {
        return memberRepository.findById(id).orElseThrow { MemberInputException("존재하지 않는 유저 입니다.") }
    }

    fun findExistingMemberByEmail(email: String): Member {
        return memberRepository.findByEmail(email) ?: throw MemberInputException("존재하지 않는 유저 입니다.")
    }
}

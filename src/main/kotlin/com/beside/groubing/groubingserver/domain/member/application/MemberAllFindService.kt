package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberFindResponse
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberAllFindService(
    private val memberRepository: MemberRepository
) {
    fun findAllMembers(): List<MemberFindResponse> {
        return memberRepository.findAll(Sort.by(Sort.Direction.ASC, "nickname"))
            .map { MemberFindResponse(it) }
    }

}

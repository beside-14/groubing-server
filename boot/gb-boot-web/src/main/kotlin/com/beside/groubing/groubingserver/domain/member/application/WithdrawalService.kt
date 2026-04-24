package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.bingo.domain.port.BingoBoardCommandRepository
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WithdrawalService(
    private val memberQueryRepository: MemberQueryRepository,
    private val memberCommandRepository: MemberCommandRepository,
    private val bingoBoardCommandRepository: BingoBoardCommandRepository
) {
    fun withdrawal(memberId: Long) {
        val member = memberQueryRepository.findById(memberId)
        memberCommandRepository.update(member.withdrawn())
        bingoBoardCommandRepository.inactivateAllOf(memberId)
    }
}

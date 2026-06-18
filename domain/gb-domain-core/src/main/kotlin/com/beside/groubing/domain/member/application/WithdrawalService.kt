package com.beside.groubing.domain.member.application

import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WithdrawalService(
    private val memberQueryRepository: MemberQueryRepository,
    private val memberCommandRepository: MemberCommandRepository
) {
    fun withdrawal(memberId: Long) {
        val member = memberQueryRepository.findById(memberId)
        member.validateWithdrawable()
        memberCommandRepository.withdraw(memberId)
    }
}

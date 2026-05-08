package com.beside.groubing.domain.member.application

import com.beside.groubing.domain.member.domain.NicknameUniquenessValidator
import com.beside.groubing.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberNicknameEditService(
    private val memberQueryRepository: MemberQueryRepository,
    private val memberCommandRepository: MemberCommandRepository,
    private val nicknameUniquenessValidator: NicknameUniquenessValidator
) {
    @Transactional
    fun edit(id: Long, nickname: String) {
        nicknameUniquenessValidator.validate(nickname)
        val member = memberQueryRepository.findById(id)
        memberCommandRepository.update(member.withNickname(nickname))
    }
}

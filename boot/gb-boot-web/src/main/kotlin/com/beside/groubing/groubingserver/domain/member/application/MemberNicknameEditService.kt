package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.port.MemberCommandRepository
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberNicknameEditService(
    private val memberQueryRepository: MemberQueryRepository,
    private val memberCommandRepository: MemberCommandRepository
) {
    @Transactional
    fun edit(id: Long, nickname: String) {
        if (memberQueryRepository.existsByNickname(nickname)) {
            throw MemberInputException("이미 사용 중인 닉네임 입니다.")
        }
        val member = memberQueryRepository.findById(id)
        memberCommandRepository.update(member.withNickname(nickname))
    }
}

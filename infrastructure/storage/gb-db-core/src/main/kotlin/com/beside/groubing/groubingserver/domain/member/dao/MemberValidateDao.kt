package com.beside.groubing.groubingserver.domain.member.dao

import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.repository.MemberJpaRepository
import org.springframework.stereotype.Repository

@Repository
class MemberValidateDao(
    private val memberJpaRepository: MemberJpaRepository
) {
    fun validateDuplicateNickname(nickname: String) {
        if (memberJpaRepository.existsByNickname(nickname)) {
            throw MemberInputException("이미 사용 중인 닉네임 입니다.")
        }
    }

    fun validateDuplicateEmail(email: String) {
        if (memberJpaRepository.existsByEmail(email)) {
            throw MemberInputException("이미 가입된 email 주소입니다.")
        }
    }

    fun validateExistingMembers(ids: List<Long>) {
        if (memberJpaRepository.countByIdIn(ids) != ids.size) {
            throw MemberInputException("입력된 ID 중 존재하지 않는 회원이 있습니다. memberIds:${ids}")
        }
    }
}

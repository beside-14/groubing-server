package com.beside.groubing.groubingserver.domain.member.dao

import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.repository.MemberJpaRepository
import org.springframework.stereotype.Repository

@Repository
class MemberValidateDao(
    private val memberJpaRepository: MemberJpaRepository
) {
    fun validateExistingMembers(ids: List<Long>) {
        if (memberJpaRepository.countByIdIn(ids) != ids.size) {
            throw MemberInputException("입력된 ID 중 존재하지 않는 회원이 있습니다. memberIds:${ids}")
        }
    }
}

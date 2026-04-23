package com.beside.groubing.groubingserver.domain.member.domain.port

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.NewMember

interface MemberCommandRepository {
    fun save(newMember: NewMember): Member

    fun update(member: Member): Member
}

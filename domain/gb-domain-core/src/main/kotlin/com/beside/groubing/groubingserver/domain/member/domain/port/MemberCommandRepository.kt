package com.beside.groubing.groubingserver.domain.member.domain.port

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.NewMember
import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo

interface MemberCommandRepository {
    fun save(newMember: NewMember): Member

    fun update(member: Member): Member

    fun editProfile(memberId: Long, newProfile: FileInfo): FileInfo?

    fun deleteProfile(memberId: Long): FileInfo?
}

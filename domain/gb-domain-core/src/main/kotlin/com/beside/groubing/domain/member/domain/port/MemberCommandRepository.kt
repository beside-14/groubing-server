package com.beside.groubing.domain.member.domain.port

import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.NewMember
import com.beside.groubing.domain.common.file.domain.FileInfo

interface MemberCommandRepository {
    fun save(newMember: NewMember): Member

    fun update(member: Member): Member

    fun editProfileOrNull(memberId: Long, newProfile: FileInfo): FileInfo?

    fun deleteProfileOrNull(memberId: Long): FileInfo?
}

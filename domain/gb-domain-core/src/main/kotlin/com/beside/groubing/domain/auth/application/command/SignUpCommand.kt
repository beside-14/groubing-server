package com.beside.groubing.domain.auth.application.command

import com.beside.groubing.domain.auth.domain.port.PasswordEncryptor
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.domain.NewMember

class SignUpCommand(
    val loginId: String,
    val password: String,
    val nickname: String
) {
    fun toNewMember(passwordEncryptor: PasswordEncryptor): NewMember = NewMember(
        loginId = loginId,
        password = passwordEncryptor.encode(password),
        nickname = nickname,
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC
    )
}

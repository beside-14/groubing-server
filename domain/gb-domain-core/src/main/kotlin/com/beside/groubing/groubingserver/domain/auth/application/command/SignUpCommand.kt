package com.beside.groubing.groubingserver.domain.auth.application.command

import com.beside.groubing.groubingserver.domain.auth.domain.port.PasswordEncryptor
import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import com.beside.groubing.groubingserver.domain.member.domain.MemberType
import com.beside.groubing.groubingserver.domain.member.domain.NewMember

class SignUpCommand(
    val email: String,
    val password: String,
    val nickname: String
) {
    fun toNewMember(passwordEncryptor: PasswordEncryptor): NewMember = NewMember(
        email = email,
        password = passwordEncryptor.encode(password),
        nickname = nickname,
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC
    )
}

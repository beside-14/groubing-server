package com.beside.groubing.domain.member.exception

class MemberInputException(
    override val message: String
) : RuntimeException("[MemberInputException] $message")

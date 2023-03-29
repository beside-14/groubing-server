package com.beside.groubing.groubingserver.domain.member.exception

class MemberInputException(
        override val message: String
) : RuntimeException("[MemberInputException] $message")
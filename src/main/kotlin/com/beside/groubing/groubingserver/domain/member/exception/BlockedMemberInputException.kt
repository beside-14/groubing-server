package com.beside.groubing.groubingserver.domain.member.exception

class BlockedMemberInputException(
    override val message: String
) : RuntimeException("[BlockedMemberInputException] $message")

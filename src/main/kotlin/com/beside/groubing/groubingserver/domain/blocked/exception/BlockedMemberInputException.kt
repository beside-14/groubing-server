package com.beside.groubing.groubingserver.domain.blocked.exception

class BlockedMemberInputException(
    override val message: String
) : RuntimeException("[BlockedMemberInputException] $message")

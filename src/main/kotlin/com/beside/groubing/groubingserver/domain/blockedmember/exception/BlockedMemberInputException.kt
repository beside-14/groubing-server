package com.beside.groubing.groubingserver.domain.blockedmember.exception

class BlockedMemberInputException(
    override val message: String
) : RuntimeException("[BlockedMemberInputException] $message")

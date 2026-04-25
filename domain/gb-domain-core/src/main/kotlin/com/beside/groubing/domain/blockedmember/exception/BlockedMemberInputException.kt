package com.beside.groubing.domain.blockedmember.exception

class BlockedMemberInputException(
    override val message: String
) : RuntimeException("[BlockedMemberInputException] $message")

package com.beside.groubing.groubingserver.domain.member.exception

class FriendInputException(
    override val message: String
) : RuntimeException("[FriendInputException] $message")

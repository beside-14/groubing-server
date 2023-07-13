package com.beside.groubing.groubingserver.domain.friend.exception

class FriendInputException(
    override val message: String
) : RuntimeException("[FriendInputException] $message")

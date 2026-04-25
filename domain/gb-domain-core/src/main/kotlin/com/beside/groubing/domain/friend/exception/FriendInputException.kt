package com.beside.groubing.domain.friend.exception

class FriendInputException(
    override val message: String
) : RuntimeException("[FriendInputException] $message")

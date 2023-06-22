package com.beside.groubing.groubingserver.domain.friendship.exception

class FriendshipInputException(
    override val message: String
) : RuntimeException("[FriendshipInputException] $message")

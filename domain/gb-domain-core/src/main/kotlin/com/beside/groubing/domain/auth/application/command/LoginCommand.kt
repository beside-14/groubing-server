package com.beside.groubing.domain.auth.application.command

class LoginCommand(
    val loginId: String,
    val password: String,
    val fcmToken: String?
)

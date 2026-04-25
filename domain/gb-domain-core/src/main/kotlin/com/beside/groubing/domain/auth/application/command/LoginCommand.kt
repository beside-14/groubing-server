package com.beside.groubing.domain.auth.application.command

class LoginCommand(
    val email: String,
    val password: String,
    val fcmToken: String?
)

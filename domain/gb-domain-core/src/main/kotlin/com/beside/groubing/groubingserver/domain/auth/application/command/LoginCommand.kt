package com.beside.groubing.groubingserver.domain.auth.application.command

class LoginCommand(
    val email: String,
    val password: String,
    val fcmToken: String?
)

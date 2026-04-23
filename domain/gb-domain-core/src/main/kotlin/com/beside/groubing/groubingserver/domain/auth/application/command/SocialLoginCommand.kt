package com.beside.groubing.groubingserver.domain.auth.application.command

import com.beside.groubing.groubingserver.domain.auth.domain.SocialType

class SocialLoginCommand(
    val id: String,
    val email: String?,
    val socialType: SocialType,
    val fcmToken: String?
)

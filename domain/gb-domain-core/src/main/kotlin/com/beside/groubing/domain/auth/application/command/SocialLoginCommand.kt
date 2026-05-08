package com.beside.groubing.domain.auth.application.command

import com.beside.groubing.domain.auth.domain.SocialType

class SocialLoginCommand(
    val id: String,
    val email: String?,
    val socialType: SocialType,
    val fcmToken: String?
)

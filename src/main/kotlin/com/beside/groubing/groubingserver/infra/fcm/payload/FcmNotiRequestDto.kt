package com.beside.groubing.groubingserver.infra.fcm.payload

import com.beside.groubing.groubingserver.domain.notification.domain.ScreenType

class FcmNotiRequestDto(
    val fcmToken: String,

    val title: String,

    val message: String,

    val screenType: ScreenType,

    val dataId: Long
)

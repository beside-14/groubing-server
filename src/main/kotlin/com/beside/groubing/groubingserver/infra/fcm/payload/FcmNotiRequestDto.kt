package com.beside.groubing.groubingserver.infra.fcm.payload

class FcmNotiRequestDto(
    val fcmToken: String,

    val title: String,

    val message: String
)

package com.beside.groubing.groubingserver.infra.fcm.application

import com.beside.groubing.groubingserver.infra.fcm.payload.FcmNotiRequestDto
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service

@Service
class FcmNotiService(
    private val firebaseMessaging: FirebaseMessaging
) {
    fun sendNotificationMessage(fcmNotiRequestDtos: List<FcmNotiRequestDto>) {
        fcmNotiRequestDtos.forEach { sendNotificationMessage(it) }
    }

    fun sendNotificationMessage(fcmNotiRequestDto: FcmNotiRequestDto) {
        val notification = Notification.builder()
            .setTitle(fcmNotiRequestDto.title)
            .setBody(fcmNotiRequestDto.message)
            .build()

        val message = Message.builder()
            .setToken(fcmNotiRequestDto.fcmToken)
            .setNotification(notification)
            .build()
        println("message: $message, fcmToken: ${fcmNotiRequestDto.fcmToken}, title: ${fcmNotiRequestDto.message}")
        firebaseMessaging.send(message)
    }
}

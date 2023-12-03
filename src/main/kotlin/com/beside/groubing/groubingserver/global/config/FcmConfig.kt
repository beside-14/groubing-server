package com.beside.groubing.groubingserver.global.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseApp.DEFAULT_APP_NAME
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
class FcmConfig {
    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        val existingApp = findExistingFirebaseApp()
        if (existingApp != null) {
            return FirebaseMessaging.getInstance(existingApp)
        }
        return FirebaseMessaging.getInstance(initializeNewFirebaseApp())
    }

    private fun findExistingFirebaseApp(): FirebaseApp? {
        return FirebaseApp.getApps().find { it.name == DEFAULT_APP_NAME }
    }

    private fun initializeNewFirebaseApp(): FirebaseApp {
        val firebaseOptions = FirebaseOptions.builder()
            .setCredentials(loadGoogleCredentials())
            .build()
        return FirebaseApp.initializeApp(firebaseOptions)
    }

    private fun loadGoogleCredentials(): GoogleCredentials {
        val classPathResource = ClassPathResource("firebase/groubing-68b92-firebase-adminsdk-45t01-ac2011f529.json")
        return GoogleCredentials.fromStream(classPathResource.inputStream)
    }
}

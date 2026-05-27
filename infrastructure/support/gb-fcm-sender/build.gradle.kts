plugins {
    `java-library`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    // FcmConfig 가 FirebaseMessaging 빈을 생성하는 데에만 사용하고 타입이 외부로 새지 않으므로 implementation.
    implementation("com.google.firebase:firebase-admin:9.2.0")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

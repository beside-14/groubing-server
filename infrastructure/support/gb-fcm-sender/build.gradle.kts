plugins {
    `java-library`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    // firebase-admin은 boot-web 의 GlobalExceptionHandler 가 FirebaseMessagingException
    // 을 직접 참조하므로 api 로 노출한다.
    api("com.google.firebase:firebase-admin:9.2.0")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

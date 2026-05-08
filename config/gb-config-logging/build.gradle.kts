// Logback 설정을 classpath에 제공하는 리소스 전용 모듈.
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

dependencies {
    implementation(project(":domain:gb-domain-core"))

    // PasswordEncryptor 포트 구현을 위해 Spring Security crypto 의존.
    implementation("org.springframework.boot:spring-boot-starter-security")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

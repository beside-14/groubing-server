plugins {
    `java-library`
}

dependencies {
    // 도메인 컴포넌트(@Component)와 @Transactional 사용을 위해 Spring Boot / Spring TX 를 노출한다.
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework:spring-tx")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

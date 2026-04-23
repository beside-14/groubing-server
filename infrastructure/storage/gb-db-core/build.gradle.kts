plugins {
    `java-library`
}

apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

dependencies {
    // 순수 도메인 모델 / 포트 인터페이스
    api(project(":domain:gb-domain-core"))

    // 엔티티는 boot-web(그리고 추후 domain) 에 남기 때문에 JPA / QueryDSL API 는 api 로 노출한다.
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("com.querydsl:querydsl-jpa:5.1.0:jakarta")

    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    // AuditingConfig 가 SecurityContextHolder 로부터 actor 를 읽는다.
    implementation("org.springframework.boot:spring-boot-starter-security")

    // 런타임 전용 - 로그 포매팅 및 JDBC 드라이버
    runtimeOnly("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.2")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

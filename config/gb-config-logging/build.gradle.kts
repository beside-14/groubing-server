// 로깅 모듈: logback 설정(logback.xml) + 로깅 파사드(kotlin-logging)를 프로젝트 전역에 제공한다.
plugins {
    `java-library`
}

dependencies {
    // 로깅 파사드는 의존 모듈이 직접 호출하므로 api 로 노출(전이).
    api("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

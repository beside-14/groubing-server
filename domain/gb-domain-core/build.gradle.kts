plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // 도메인 컴포넌트(@Component)와 @Transactional 사용을 위해 Spring Boot / Spring TX 를 노출한다.
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework:spring-tx")

    // testFixtures: 도메인 픽스처에서 Arb 사용
    testFixturesImplementation("io.kotest:kotest-property-jvm:5.5.5")

    // testFixtures: KotestConfig 가 spring extension 을 등록한다.
    testFixturesApi("io.kotest:kotest-runner-junit5-jvm:5.5.5")
    testFixturesApi("io.kotest.extensions:kotest-extensions-spring:1.1.2")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

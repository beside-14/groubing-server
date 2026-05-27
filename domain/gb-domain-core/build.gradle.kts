plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // @Component/@Service/@Transactional(런타임 리텐션 애노테이션)에만 사용되고
    // 공개 시그니처에 Spring 타입이 노출되지 않으므로 implementation 으로 충분하다.
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework:spring-tx")

    // testFixtures: 도메인 픽스처에서 Arb 사용
    testFixturesImplementation("io.kotest:kotest-property-jvm:5.5.5")

    // testFixtures: KotestConfig 가 spring extension 을 등록한다.
    // (소비 모듈은 root subprojects 의 testImplementation 으로 kotest 를 직접 갖는다.)
    testFixturesImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
    testFixturesImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

plugins {
    `java-library`
    `java-test-fixtures`
}

apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

dependencies {
    // 순수 도메인 모델 / 포트 인터페이스 (유일한 소비자 boot-web 이 domain-core 를 직접 의존하므로 재노출 불필요)
    implementation(project(":domain:gb-domain-core"))

    // 엔티티/Q클래스가 이 모듈에 있어 JPA/QueryDSL 타입이 외부로 새지 않으므로 implementation 으로 충분하다.
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")

    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    // AuditingConfig 가 SecurityContextHolder 로부터 actor 를 읽는다.
    implementation("org.springframework.boot:spring-boot-starter-security")

    // 런타임 전용 - 로그 포매팅 및 JDBC 드라이버
    runtimeOnly("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.2")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    // 슬라이스 테스트(@PersistenceTest)가 application-test.yml 을 로드하므로
    // yaml-importer 모듈이 test classpath 에 필요하다.
    testRuntimeOnly(project(":config:gb-config-yaml-importer"))

    // testFixtures: 엔티티 픽스처(MemberEntity 의 aMember(...)) 가 Arb 사용
    testFixturesImplementation("io.kotest:kotest-property-jvm:5.5.5")

    // db-core 자체 슬라이스 테스트에서 도메인 픽스처(BingoBoardFixtures 등)가 필요하다.
    testImplementation(testFixtures(project(":domain:gb-domain-core")))
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

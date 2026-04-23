plugins {
    `java-library`
}

dependencies {
    // 순수 도메인 모듈 — JPA/QueryDSL/Spring Web 의존 없음.
    // 트랜잭션이나 이벤트 퍼블리싱 등이 필요해지면 spring-tx / spring-context 를 추가한다.
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

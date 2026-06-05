dependencies {
    implementation(project(":domain:gb-domain-core"))

    // @Component / @Value 사용을 위한 spring-context. domain-core 가 implementation 으로
    // starter 를 보유하지만 transitive 로 노출되지 않으므로 본 모듈도 직접 선언한다.
    implementation("org.springframework.boot:spring-boot-starter")

    // HashidsIdObfuscator 구현체 — id ↔ 짧은 문자열 양방향 obfuscation.
    implementation("org.hashids:hashids:1.0.3")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

// 프로파일별 YAML 설정을 classpath에 제공하는 리소스 전용 모듈.
// Spring Boot 기본 메커니즘으로 application.yml / application-{profile}.yml 이 자동 로드된다.
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

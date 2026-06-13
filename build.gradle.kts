import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

allprojects {
    group = "com.beside.groubing"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    // 컨테이너 디렉터리(boot, infrastructure/storage 등)는 실제 모듈이 아니므로 플러그인 적용 대상 제외
    if (childProjects.isNotEmpty()) return@subprojects

    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "jacoco")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencies {
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib")

        "testImplementation"("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
            exclude(module = "mockito-core")
        }
        "testImplementation"("com.ninja-squad:springmockk:4.0.2")
        "testImplementation"("io.kotest:kotest-runner-junit5-jvm:5.5.5")
        "testImplementation"("io.kotest:kotest-assertions-core-jvm:5.5.5")
        "testImplementation"("io.kotest:kotest-extensions-jvm:5.5.5")
        "testImplementation"("io.kotest:kotest-property-jvm:5.5.5")
        "testImplementation"("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JavaVersion.VERSION_21.toString()
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.withType<JacocoReport>())
    }

    extensions.configure<JacocoPluginExtension> {
        toolVersion = "0.8.12"
    }

    tasks.withType<JacocoReport> {
        dependsOn(tasks.withType<Test>())
        reports {
            html.required.set(true)
            xml.required.set(true)
        }
        // 생성·무로직 코드는 커버리지 측정에서 제외
        classDirectories.setFrom(
            files(classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/Q*.class",        // QueryDSL kapt 생성물
                        "**/*Application*",   // 부트 진입점
                        "**/config/**",
                        "**/*Config*",
                        "**/entity/**",       // JPA 엔티티
                        "**/payload/**"       // 요청/응답 DTO
                    )
                }
            })
        )
    }
}

// 루트 프로젝트는 실행 가능한 산출물이 아님
tasks.named("bootJar") {
    enabled = false
}

tasks.named("jar") {
    enabled = false
}

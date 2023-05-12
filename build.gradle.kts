import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.0.3"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"
    kotlin("plugin.jpa") version "1.8.10"
    kotlin("kapt") version "1.8.10"
}

group = "com.beside.groubing"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // MySql
    runtimeOnly("com.mysql:mysql-connector-j")

    // H2
    runtimeOnly("com.h2database:h2")

    // Test
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    // Mockk
    testImplementation("com.ninja-squad:springmockk:4.0.0")

    // KoTest
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
    testImplementation("io.kotest:kotest-extensions-jvm:5.5.5")
    testImplementation("io.kotest:kotest-property-jvm:5.5.5")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")

    // AsciiDocs
    val asciidoctorExt: Configuration by configurations.creating
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

configure<org.springframework.boot.gradle.dsl.SpringBootExtension> {
    buildInfo()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    // Spring REST Docs
    val snippetsDir = file("build/generated-snippets")

    withType<Test> {
        useJUnitPlatform()
        outputs.dir(snippetsDir)
        filter {
            includeTestsMatching("com.beside.groubing.*")
        }
    }

    // AsciiDocs
    withType<AsciidoctorTask> {
        configurations("asciidoctorExt")
        inputs.dir(snippetsDir)
        dependsOn(test)
        // 빌드 시, 아래 경로의 파일 삭제
        doFirst {
            delete {
                file("build/docs/asciidoc")
                file("src/main/resources/static/docs")
            }
        }
    }

    withType<BootJar> {
        dependsOn(asciidoctor)
        from("${asciidoctor.get().outputDir}/html5") {
            into("static/docs")
        }

        // BootJar 파일명
        archiveBaseName.set("api")
        // BootJar 버전
        archiveVersion.set("")
    }

    val copyDocument by registering(Copy::class) {
        dependsOn(asciidoctor)

        from(file("build/docs/asciidoc/"))
        into(file("src/main/resources/static/docs"))
    }

    build {
        dependsOn(copyDocument)
    }
}

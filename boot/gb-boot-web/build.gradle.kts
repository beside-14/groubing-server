import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

dependencies {
    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // QueryDSL Q-클래스는 여전히 이 모듈의 엔티티들에서 생성되어야 한다.
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    // Domain
    implementation(project(":domain:gb-domain-core"))

    // Infrastructure
    implementation(project(":infrastructure:storage:gb-db-core"))
    implementation(project(":infrastructure:support:gb-jwt-core"))
    implementation(project(":infrastructure:support:gb-crypto-core"))

    // Kotlin-Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // FCM
    implementation(project(":infrastructure:support:gb-fcm-sender"))

    // Test
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    // 도메인/엔티티 픽스처 + 슬라이스 어노테이션은 각 모듈의 testFixtures 에서 가져온다.
    testImplementation(testFixtures(project(":domain:gb-domain-core")))
    testImplementation(testFixtures(project(":infrastructure:storage:gb-db-core")))

    // Config modules
    runtimeOnly(project(":config:gb-config-yaml-importer"))
    runtimeOnly(project(":config:gb-config-logging"))

    // AsciiDocs
    val asciidoctorExt: Configuration by configurations.creating
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

configure<org.springframework.boot.gradle.dsl.SpringBootExtension> {
    buildInfo()
}

tasks {
    val snippetsDir = file("build/generated-snippets")

    withType<Test> {
        outputs.dir(snippetsDir)
        filter {
            includeTestsMatching("com.beside.groubing.*")
        }
    }

    withType<AsciidoctorTask> {
        configurations("asciidoctorExt")
        inputs.dir(snippetsDir)
        dependsOn(test)
        baseDirFollowsSourceFile()
        doFirst {
            delete {
                file("build/docs/asciidoc")
                file("src/main/resources/static/docs")
            }
        }
        forkOptions {
            jvmArgs(
                "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
                "--add-opens", "java.base/java.io=ALL-UNNAMED"
            )
        }
    }

    withType<BootJar> {
        dependsOn(asciidoctor)
        from("${asciidoctor.get().outputDir}/html5") {
            into("static/docs")
        }

        archiveBaseName.set("api")
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

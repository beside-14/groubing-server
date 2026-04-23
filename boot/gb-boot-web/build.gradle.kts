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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    // JWT
    implementation(project(":infrastructure:support:gb-jwt-core"))

    // MySql
    runtimeOnly("com.mysql:mysql-connector-j")

    // H2
    runtimeOnly("com.h2database:h2")

    // p6spy
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.2")

    // Kotlin-Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // FCM
    implementation(project(":infrastructure:support:gb-fcm-sender"))

    // Test
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

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

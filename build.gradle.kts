import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("kapt") version "2.2.0"
    kotlin("plugin.jpa") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("com.google.protobuf") version "0.9.4"
    id("org.flywaydb.flyway") version "11.13.2"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("it.nicolasfarabegoli.conventional-commits") version "3.1.3"
}

group = "org.careerseekers"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("io.projectreactor:reactor-test")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Spring security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // Spring WebSocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.webjars:stomp-websocket:2.3.3")

    // JWT Auth
    implementation("io.jsonwebtoken:jjwt:0.12.6")
    implementation("javax.xml.bind:jaxb-api:2.3.0")

    // Databases
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Mapper
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.3.Final")

    // Kafka messaging
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // gRPC messaging
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")
    implementation("net.devh:grpc-client-spring-boot-starter:3.1.0.RELEASE")
    implementation("com.google.protobuf:protobuf-java:4.28.2")
    implementation("io.grpc:grpc-protobuf:1.57.2")
    implementation("io.grpc:grpc-stub:1.57.2")

    //Kotlinx coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    //Kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    // Utilities
    implementation("one.stayfocused.spring:dotenv-spring-boot:1.0.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.aspectj:aspectjweaver")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk-agent-jvm:1.13.7")
    testImplementation("io.mockk:mockk:1.13.7")

    // Metrics
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // Netty
    val nettyVersion = "4.1.122.Final"

    val osName = System.getProperty("os.name").lowercase()
    val osArch = System.getProperty("os.arch").lowercase()

    if (osName.contains("mac") && osArch == "aarch64") {
        runtimeOnly("io.netty:netty-resolver-dns-native-macos:$nettyVersion:osx-aarch_64")
    } else if (osName.contains("mac")) {
        runtimeOnly("io.netty:netty-resolver-dns-native-macos:$nettyVersion:osx-x86_64")
    }

}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:11.13.2")
    }
}

flyway {
    url = ""
    user = ""
    password = ""
    locations = arrayOf("classpath:db/migration")
    schemas = arrayOf("public")
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:4.28.2" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.57.2" }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")

    filter {
        if (project.hasProperty("excludeTests")) {
            excludeTestsMatching(project.property("excludeTests") as String)
        }
    }
}

conventionalCommits {
    warningIfNoGitRoot = true
    types += listOf("build", "chore", "docs", "feat", "fix", "refactor", "style", "test")
    scopes = emptyList()
    successMessage = "Сообщение коммита соответствует стандартам Conventional Commit."
    failureMessage = "Сообщение коммита не соответствует стандартам Conventional Commit."
}

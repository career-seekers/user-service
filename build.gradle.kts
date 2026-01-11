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

val websocketVersion: String by project
val graphqlVersion: String by project
val jsonwebtokenVersion: String by project
val jaxbApiVersion: String by project
val mapstructVersion: String by project
val grpcMessagingVersion: String by project
val protobufVersion: String by project
val grpcProtobufVersion: String by project
val kotlinxSerializationVersion: String by project
val kotlinxCoroutinesVersion: String by project
val dotenvSpringVersion: String by project
val javaxAnnotationVersion: String by project
val mockkVersion: String by project

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
    implementation("org.webjars:stomp-websocket:$websocketVersion")

    // Spring GraphQL
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("com.netflix.graphql.dgs.codegen:graphql-dgs-codegen-gradle:$graphqlVersion")
    testImplementation("org.springframework.graphql:spring-graphql-test")

    // JWT Auth
    implementation("io.jsonwebtoken:jjwt:$jsonwebtokenVersion")
    implementation("javax.xml.bind:jaxb-api:$jaxbApiVersion")

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
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // Kafka messaging
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // gRPC messaging
    implementation("net.devh:grpc-server-spring-boot-starter:$grpcMessagingVersion")
    implementation("net.devh:grpc-client-spring-boot-starter:$grpcMessagingVersion")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("io.grpc:grpc-protobuf:$grpcProtobufVersion")
    implementation("io.grpc:grpc-stub:$grpcProtobufVersion")

    //Kotlinx coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    //Kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    // Utilities
    implementation("one.stayfocused.spring:dotenv-spring-boot:$dotenvSpringVersion")
    implementation("javax.annotation:javax.annotation-api:$javaxAnnotationVersion")
    implementation("org.aspectj:aspectjweaver")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk-agent-jvm:$mockkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

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

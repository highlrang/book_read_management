plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt") version "1.9.25"
}

group = "com.liber"
version = "0.0.1-SNAPSHOT"
description = "read_log_api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
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

extra["springCloudVersion"] = "2025.0.0"

sourceSets {
    main {
        java {
            srcDir("build/generated/source/kapt/main")
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // QueryDSL 의존성
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    // QueryDSL APT 설정(Q 클래스 생성)
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")

    // Jakarta APT용 의존성
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")

    implementation(platform("software.amazon.awssdk:bom:2.27.12"))
    implementation("software.amazon.awssdk:secretsmanager")
    implementation("software.amazon.awssdk:regions")

    implementation("com.google.genai:google-genai:1.0.0")

    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("org.springframework.boot:spring-boot-starter-mail")

}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
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
}

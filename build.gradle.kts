plugins {
    java
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jsonschema2pojo") version "1.2.1"
    id("jacoco")
    kotlin("jvm")
}

group = "faang.school"
version = "1.0"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    /**
     * Spring boot starters
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.2.0")
    implementation("org.springframework.kafka:spring-kafka")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    /**
     * Database
     */
    implementation("org.liquibase:liquibase-core")
    implementation("redis.clients:jedis:4.3.2")
    runtimeOnly("org.postgresql:postgresql")

    /**
     * Amazon S3
     */
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.464")

    /**
     * Amazon S3
     */
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.464")

    /**
     * Utils & Logging
     */
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.13.0")
    implementation("org.springframework.retry:spring-retry:2.0.2")

    /**
     * Test containers
     */
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers:1.19.3")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }

    classDirectories.setFrom(
            files(classDirectories.files.map {
                fileTree(it) {
                    exclude(
                            "**/config/**",
                            "**/controller/**",
                            "**/dto/**",
                            "**/entity/**",
                            "**/model/**",
                            "**/repository/**",
                            "**/exception/**",
                            "**/facade/**",
                            "**/handler/**",
                            "**/client/**",
                            "**/mapper/**",
                            "**/utils/**",
                            "**/job/**",
                            "**/initialization/**",
                            "**/converter/**"
                    )
                }
            })
    )
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.test)
    violationRules {
        rule {
            element = "PACKAGE"
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }

            excludes = listOf(
                    "default",
                    "school.faang.user_service.config",
                    "school.faang.user_service.config.*",
                    "school.faang.user_service.controller",
                    "school.faang.user_service.controller.*",
                    "school.faang.user_service.dto",
                    "school.faang.user_service.dto.*",
                    "school.faang.user_service.entity",
                    "school.faang.user_service.entity.*",
                    "school.faang.user_service.model",
                    "school.faang.user_service.model.*",
                    "school.faang.user_service.repository",
                    "school.faang.user_service.repository.*",
                    "school.faang.user_service.exception",
                    "school.faang.user_service.exception.*",
                    "school.faang.user_service.facade",
                    "school.faang.user_service.facade.*",
                    "school.faang.user_service.handler",
                    "school.faang.user_service.handler.*",
                    "school.faang.user_service.client",
                    "school.faang.user_service.client.*",
                    "school.faang.user_service.mapper",
                    "school.faang.user_service.mapper.*",
                    "school.faang.user_service.job",
                    "school.faang.user_service.job.*",
                    "school.faang.user_service.initialization",
                    "school.faang.user_service.initialization.*",
                    "school.faang.user_service.converter",
                    "school.faang.user_service.converter.*",
                    "school.faang.user_service.utils",
                    "school.faang.user_service.utils.*",
                    "school.faang.user_service"
            )
        }
    }
}

tasks.check {
    dependsOn(tasks.named("jacocoTestCoverageVerification"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}

kotlin {
    jvmToolchain(17)
}
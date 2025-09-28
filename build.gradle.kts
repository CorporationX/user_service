plugins {
    java
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jsonschema2pojo") version "1.2.1"
    kotlin("jvm")
    checkstyle
    jacoco
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
     *
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")


    // Другие зависимости вашего проекта

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
     * MinIO
     */
    implementation("io.minio:minio:8.5.11")

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

    /**
     * Test containers
     */
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")
    testImplementation("org.testcontainers:minio:1.19.5")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.mockito:mockito-core:5.1.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.1.1")
    testImplementation("org.mockito:mockito-inline:5.1.1")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
}

jsonSchema2Pojo {
    setSource(files("src/main/resources/json"))
    targetDirectory = file("${project.buildDir}/generated-sources/js2p")
    targetPackage = "com.json.student"
    setSourceType("jsonschema")
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/config/**",
                    "**/dto/**",
                    "**/entity/**",
                    "**/*Application*",
                    "**/exception/**",
                    "**/mapper/**Impl*",
                    "**/*MapperImpl*",
                    "**/model/**",
                    "**/enumeration/**"
                )
            }
        })
    )

    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/config/**",
                    "**/dto/**",
                    "**/entity/**",
                    "**/*Application*",
                    "**/exception/**",
                    "**/mapper/**Impl*",
                    "**/*MapperImpl*",
                    "**/model/**",
                    "**/enumeration/**"
                )
            }
        })
    )

    violationRules {
        rule {
            element = "PACKAGE"
            includes = listOf(
                "school.faang.user_service.service.goal.GoalInvitationServiceImpl"
            )

            limit {
                minimum = "0.80".toBigDecimal()
            }

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}

val test by tasks.getting(Test::class) {
    testLogging.showStandardStreams = true

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
        showCauses = true
        showExceptions = true
        showStackTraces = true
    }
}

tasks.bootJar {
    archiveFileName.set("service.jar")
}

kotlin {
    jvmToolchain(17)
}

checkstyle {
    toolVersion = "10.17.0"
    configFile = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
    checkstyle.enableExternalDtdLoad.set(true)
}

tasks.checkstyleMain {
    source = fileTree("${project.rootDir}/src/main/java")
    include("**/*.java")
    exclude("**/resources/**")

    classpath = files()
}

tasks.checkstyleTest {
    source = fileTree("${project.rootDir}/src/test")
    include("**/*.java")

    classpath = files()
}
tasks.withType<JavaCompile>().configureEach {
    if (name.contains("Test", ignoreCase = true)) {
        destinationDirectory.set(file("$buildDir/classes/java/test"))
        options.compilerArgs.addAll(
            listOf("-s", "$buildDir/generated/sources/annotationProcessor/java/test")
        )
    } else {
        destinationDirectory.set(file("$buildDir/classes/java/main"))
        options.compilerArgs.addAll(
            listOf("-s", "$buildDir/generated/sources/annotationProcessor/java/main")
        )
    }
}

tasks.register<Test>("unitTest") {
    description = "запускает только unit-тесты"
    group = "verification"
    useJUnitPlatform {
        excludeTags("integration")
    }
    include("**/*Test.class")
    exclude("**/*IntegrationTest.class")
}
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.*

plugins {
    id("java")
    id("jacoco")
    id("checkstyle")
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jsonschema2pojo") version "1.2.1"
    kotlin("jvm")
}

jacoco {
    toolVersion = "0.8.7"
}

checkstyle {
    toolVersion = "10.12.1"
}

tasks.withType<JavaCompile> {
    options.isIncremental = true
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
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
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.keycloak:keycloak-admin-client:22.0.1")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

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
    implementation("io.minio:minio:8.5.17")
    implementation("net.coobird:thumbnailator:0.4.20")

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
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.webjars:webjars-locator-core")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.13.0")

    /**
     * Test containers
     */
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

jsonSchema2Pojo {
    setSource(files("src/main/resources/json"))
    targetDirectory = file("${project.buildDir}/generated-sources/js2p")
    targetPackage = "com.json.student"
    setSourceType("jsonschema")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}

val test by tasks.getting(Test::class) { }

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    violationRules {
        rule {
            element = "CLASS"
            includes = listOf("school.faang.user_service.service.*")
            excludes = listOf("school.faang.user_service.service.goal.filter.*",
                "school.faang.user_service.service.external.*",
                "school.faang.user_service.service.SkillRequestService",
                "school.faang.user_service.service.EventService",
                "school.faang.user_service.service.filter.*",
                "school.faang.user_service.service.recommendation.RecommendationRequestFilter",
                "school.faang.user_service.service.RatingService")
            limit {
                minimum = 0.4.toBigDecimal()
            }
        }
    }
}

tasks.bootJar {
    archiveFileName.set("service.jar")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JacocoReport> {
    val filteredClassDirectories = classDirectories.files.map { dir ->
        project.fileTree(dir) {
            exclude(
                "school/faang/user_service/client",
                "school/faang/user_service/config",
                "school/faang/user_service/controller",
                "school/faang/user_service/dto",
                "school/faang/user_service/mapper"
            )
        }
    }
    classDirectories.setFrom(filteredClassDirectories)
}

tasks {
    val jacocoCustomTestReport by creating(JacocoReport::class) {
        reports {
            xml.isEnabled = false
            csv.isEnabled = false
            html.isEnabled = true
        }
    }

    withType<Test> {
        finalizedBy(jacocoCustomTestReport)
    }
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

tasks.bootJar {
    archiveFileName.set("service.jar")
}

kotlin {
    jvmToolchain(17)
}
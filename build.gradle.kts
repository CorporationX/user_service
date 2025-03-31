plugins {
    java
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jsonschema2pojo") version "1.2.1"
    kotlin("jvm")
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
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    // https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka
    implementation ("org.springframework.kafka:spring-kafka:3.3.1")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.4.2")
    implementation("org.springframework.kafka:spring-kafka:3.0.6")
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
    implementation("software.amazon.awssdk:s3:2.20.120")

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
    implementation("org.imgscalr:imgscalr-lib:4.2")
    implementation ("org.apache.commons:commons-collections4:4.4")

    /**
     * Test containers
     */
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")
    testImplementation("org.testcontainers:kafka:1.17.6")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-inline:2.13.0")
    testImplementation("org.mockito:mockito-core:5.10.0")
}

jsonSchema2Pojo {
    setSource(files("src/main/resources/json"))
    targetDirectory = file("${project.buildDir}/generated-sources/js2p")
    targetPackage = "com.json.student"
    setSourceType("jsonschema")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}
kotlin {
    jvmToolchain(17)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }

    classDirectories.setFrom(
        files(
            fileTree("build/classes/java/main")
                .exclude("**/entity/**")
                .exclude("**/com/json/**")
                .exclude("**/client/**")
                .exclude("**/config/**")
                .exclude("**/dto/**")
                .exclude("**/mapper/**")
                .exclude("**/filter/**")
        )
    )

    doLast {
        val reportDir = file("${buildDir}/reports/jacoco/test/html")
        val reportFile = file("$reportDir/index.html")

        if (reportFile.exists()) {
            println("Jacoco report generated: file://${reportFile.absolutePath}")
        } else {
            println("Jacoco report not found.")
        }
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    violationRules {
        rule {
            limit {
                minimum = 0.7.toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jsonschema2pojo") version "1.2.1"
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
    implementation ("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
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
    implementation ("io.minio:minio:8.3.2")

    /**
     * Swagger
     */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.3")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.15")
    implementation("io.springfox:springfox-boot-starter:3.0.0")

    /**
     * AOP
     */
    implementation ("org.aspectj:aspectjweaver:1.9.19")

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
    implementation("org.springframework.retry:spring-retry:2.0.3")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.13.0")
    implementation ("net.coobird:thumbnailator:0.4.1")

    implementation("org.apache.commons:commons-collections4:4.5.0-M2")
    implementation("org.quartz-scheduler:quartz:2.3.0")

    /**
     * Test containers
     */
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation(kotlin("stdlib-jdk8"))

    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

jsonSchema2Pojo {
    setSource(files("src/main/resources/json"))
    targetDirectory = file("${project.buildDir}/generated-sources/js2p")
    targetPackage = "com.json.student"
    setSourceType("jsonschema")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}

/**
 * JaCoCo settings
 */
val jacocoInclude = listOf(
    "**/service/**"
)

val jacocoExclude = listOf(
    "**/entity/**",
    "**/dto/**",
    "**/config/**",
    "**/generated/**",
    "**/exceptions/**",
    "**/service/recomendation/filters**",
    "**/service/mentorship_request/error_messages**",
    "**/service/goal/util**",
    "**/service/event/filters**",
    "**/service/mentorship_request/MentorshipRequestDescriptionFilter**"
)

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("$buildDir/reports/jacoco"))
}
tasks.test {
    exclude("**/school/faang/user_service/integration/**")
    finalizedBy(tasks.jacocoTestReport)
}
tasks.register<Test>("integrationTest") {
    group = "verification"
    include("**/school/faang/user_service/integration/**")
}
tasks.jacocoTestReport {
    reports {
        html.required.set(true)
        xml.required.set(false)
        csv.required.set(false)
    }

    // Include only the specified directories in the coverage report
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            include(jacocoInclude)
            exclude(jacocoExclude)
        }
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }
        }
    }

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            include(jacocoInclude)
            exclude(jacocoExclude)
        }
    )
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.build {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

kotlin {
    jvmToolchain(17)
}
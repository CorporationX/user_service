plugins {
    java
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jsonschema2pojo") version "1.2.1"
    id("checkstyle")
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
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
    implementation("com.google.code.findbugs:jsr305:3.0.2")

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
}

configure<CheckstyleExtension> {
    toolVersion = "10.21.4"
    config = resources.text.fromFile(file("config/checkstyle/google_checks.xml"))
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
kotlin {
    jvmToolchain(17)
}

tasks {
    withType<Checkstyle>().configureEach {
        ignoreFailures = false
        maxErrors = 0
        maxWarnings = 0
        reports {
            xml.required.set(false)
            html.required.set(true)
        }
    }

    named<Checkstyle>("checkstyleMain") {
        source = fileTree("src/main/java") {
            include("**/*.java")
            exclude("**/resources/**")
        }
        classpath = files()
    }

    named<Checkstyle>("checkstyleTest") {
        source = fileTree("src/test/java") {
            include("**/*.java")
        }
        classpath = files()
    }
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.test {
    outputs.upToDateWhen { false }  // To always rerun tests
}

// This task generates coverage report in xml format
// The report will be in build\jacocoHtml\index.html
tasks.jacocoTestReport {
    dependsOn(tasks.test)   // To start task after tests

    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

// This task verifies tests
tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)    // To start task after jacocoTestReport

    violationRules {
        rule {
            element = "CLASS"
            includes = listOf(
                "school.faang.user_service.RecommendationRequestService",
                "school.faang.user_service.SkillRequestService",
                "school.faang.user_service.SkillService",
                "school.faang.user_service.SkillService",
            )

            limit {
                counter = "LINE"    // Check line coverage
                value = "COVEREDRATIO"  // Set minimum coverage in percents
                minimum = "0.75".toBigDecimal() // Percentage value
            }

            limit {
                counter = "BRANCH"      // Check branch coverage
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
            }

            limit {
                counter = "INSTRUCTION" // Check instruction (all byte-code instructions) coverage
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
            }
        }
    }
}

// Set dependencies for task "check" to run JaCoCo by one command ./gradlew check
tasks.check {
    dependsOn(tasks.jacocoTestReport)
    dependsOn(tasks.jacocoTestCoverageVerification)
}

// To run check after build gradle
tasks.build {
    dependsOn(tasks.check)
}

// To run check after rebuild
tasks.classes {
    finalizedBy(tasks.check)
}

// To run check after rebuild
tasks.compileJava {
    finalizedBy(tasks.check)
}

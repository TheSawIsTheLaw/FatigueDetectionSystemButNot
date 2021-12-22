import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.11"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
    kotlin("plugin.jpa") version "1.4.32"
}

group = "com.fdsystem"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.data:spring-data-jdbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.influxdb:influxdb-client-kotlin:3.3.0")
    implementation("com.google.code.gson:gson")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.exposed:exposed-core:0.35.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.35.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.35.2")
    implementation("org.springdoc:springdoc-openapi-ui:1.5.11")
    implementation("io.jsonwebtoken:jjwt-api:0.11.1")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.1")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.1")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

sourceSets {
    create("unitTest") {
        kotlin {
            compileClasspath += main.get().output + configurations.testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }
    }

    create("integrationTest") {
        kotlin {
            compileClasspath += main.get().output + configurations.testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }
    }

    create("e2eTest") {
        kotlin {
            compileClasspath += main.get().output + configurations.testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }
    }
}

val unitTest = task<Test>("unitTest") {
    description = "Runs unit test."
    group = "verification"
    testClassesDirs = sourceSets["unitTest"].output.classesDirs
    classpath = sourceSets["unitTest"].runtimeClasspath
}

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    mustRunAfter(tasks["test"])
}

val e2eTest = task<Test>("e2eTest") {
    description = "Runs e2e test."
    group = "verification"
    testClassesDirs = sourceSets["e2eTest"].output.classesDirs
    classpath = sourceSets["e2eTest"].runtimeClasspath
    mustRunAfter(tasks["integrationTest"])
    systemProperty("numberOfExecutions", System.getProperty("nOfExecs"))
}

// Well, i've tried to automize it. Not helped.
//val runDockerCompose = task("startDatabases") {
//    doLast {
//        exec {
//            executable = "make"
//            args = listOf("runclear")
//        }
//    }
//}
//
//val runDockerD = task("runDockerDaemon") {
//    doLast {
//        exec {
//            executable = "dockerd"
//        }
//    }
//}
//
//runDockerCompose.dependsOn(runDockerD)
//
//integrationTest.dependsOn(runDockerCompose)

tasks.check {
    dependsOn(unitTest)
    dependsOn(integrationTest)
    dependsOn(e2eTest)
}

kotlin.target.compilations.getByName("test") {
    associateWith(target.compilations.getByName("main"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

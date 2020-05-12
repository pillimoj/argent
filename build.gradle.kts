import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.serialization") version "1.3.72"
    kotlin("jvm") version "1.3.72"
    id("com.google.cloud.tools.jib") version "2.2.0"
    application
}

val ktorVersion = "1.3.2"
val argentMainClass = "argent.MainKt"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val image = "argent"

group = "Argent"
version = "1.0-SNAPSHOT"

application {
    mainClassName = argentMainClass
}

kotlin.sourceSets {
        main {
            kotlin.srcDir("src")
            resources.srcDir("resources")
        }
        test {
            kotlin.srcDir("test")
            resources.srcDir("testresources")
        }
    }
sourceSets {
    main {
        resources { srcDir("resources") }
    }
}


repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Ktor
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")

    // Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("net.logstash.logback:logstash-logback-encoder:6.3")

    // Database
    val exposedVersion = "0.20.2"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.zaxxer:HikariCP:3.4.2")
    implementation("org.postgresql:postgresql:42.2.2")
    implementation("org.flywaydb:flyway-core:6.2.4")

    // Google
    implementation("com.google.cloud:google-cloud-secretmanager:1.0.0")
    implementation("com.google.cloud.sql:postgres-socket-factory:1.0.16")


    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
}

jib {
    // Use digest to make builds reproducible. gcr.io/distroless/java:8 @ 2020-04-09 00:49B
    from.image = "gcr.io/distroless/java@sha256:e99eb6cf88ca2df69e99bf853d65f125066730e3e9f7a233bd1b7e3523c144cb"
    container {
        ports = listOf("80")
        mainClass = argentMainClass

        jvmFlags = listOf(
            "-server",
            "-Djava.awt.headless=true",
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=100",
            "-XX:+UseStringDeduplication",
            "-Xmx128m",
            "-Xms128m",
            "-XX:MaxRAMPercentage=40.0"
        )
    }
    to {
        image = if (hasProperty("jibImage")) properties["jibImage"]!!.toString() else "argent"
        if (hasProperty("gitHash")) tags = setOf(properties["gitHash"]!!.toString())
    }
}


val test = tasks.withType<Test> {
    getEnvVariables().forEach { environment(it.key, it.value) }
    useJUnitPlatform {}
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.getByName<JavaExec>("run") {
    getEnvVariables().forEach { environment(it.key, it.value) }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

fun getEnvVariables(): Map<String, String> {
    return File("$projectDir/.env")
        .takeIf { it.exists() }
        ?.readLines()
        ?.filterNot { it.isEmpty() || it.startsWith("#") }
        ?.map { it.split("=").zipWithNext().first() }
        ?.filter { System.getenv(it.first).isNullOrBlank() }
        ?.toMap() ?: emptyMap()
}

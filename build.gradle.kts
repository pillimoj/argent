import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.9"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("com.google.cloud.tools.jib") version "3.4.1"
    application
}

val ktorVersion = "2.0.0"
val argentMainClass = "argent.MainKt"
val image = "argent"

group = "Argent"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
    sourceSets {
        main {
            kotlin.srcDir("src")
            resources.srcDir("resources")
        }
        test {
            kotlin.srcDir("test")
            resources.srcDir("testresources")
        }
    }
}

repositories {
    mavenCentral {}
}

dependencies {

    // Ktor
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    // Ktor server
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-call-id")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-compression")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-forwarded-header")
    implementation("io.ktor:ktor-server-hsts")
    implementation("io.ktor:ktor-server-status-pages")

    // Ktor client
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // Database
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.flywaydb:flyway-core:8.5.4")
    implementation("com.google.cloud.sql:postgres-socket-factory:1.19.0")

    // Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Google
    implementation(platform("com.google.cloud:libraries-bom:26.42.0"))
    implementation("com.google.cloud:google-cloud-secretmanager")

    // JWT
    implementation("com.auth0:java-jwt:3.19.1")

    // Tests
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks {
    test {
        getEnvVariables().forEach { environment(it.key, it.value) }
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    named<JavaExec>("run") {
        getEnvVariables().forEach { environment(it.key, it.value) }
    }

}

// PLUGINS
application {
    mainClass.set(argentMainClass)
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

jib {
    from.image = "gcr.io/distroless/java21-debian12"
    container {
        ports = listOf("80")
        mainClass = argentMainClass

        jvmFlags =
            listOf(
                "-server",
                "-Djava.awt.headless=true",
                "-XX:+UseG1GC",
                "-XX:MaxGCPauseMillis=100",
                "-XX:+UseStringDeduplication",
                "-Xmx128m",
                "-Xms128m",
            )
    }
    to {
        image = if (hasProperty("jibImage")) properties["jibImage"]!!.toString() else "argent"
        if (hasProperty("gitHash")) tags = setOf(properties["gitHash"]!!.toString())
    }
}

// HELPERS
fun getEnvVariables(): Map<String, String> {
    return File("$projectDir/.env")
        .takeIf { it.exists() }
        ?.readLines()
        ?.filterNot { it.isEmpty() || it.startsWith("#") }
        ?.map { it.split("=").zipWithNext().first() }
        ?.filter { System.getenv(it.first).isNullOrBlank() }
        ?.toMap() ?: emptyMap()
}

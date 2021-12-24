plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.google.cloud.tools.jib") version "3.1.4"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    application
}

val ktorVersion = "1.6.7"
val argentMainClass = "argent.MainKt"
val image = "argent"

group = "Argent"
version = "1.0-SNAPSHOT"

kotlin.sourceSets {
    main { kotlin.srcDir("src") }
    test { kotlin.srcDir("test") }
}
sourceSets {
    main { resources.srcDir("resources") }
    test { resources.srcDir("testresources") }
}

repositories { mavenCentral {} }

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0")

    // Ktor
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.9")
    implementation("net.logstash.logback:logstash-logback-encoder:7.0.1")

    // Database
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("org.postgresql:postgresql:42.3.1")
    implementation("org.flywaydb:flyway-core:8.2.3")

    // Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    // Secrets
    implementation("com.google.cloud:google-cloud-secretmanager:2.0.5")

    // JWT
    implementation("com.auth0:java-jwt:3.18.2")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
}

tasks {
    test {
        getEnvVariables().forEach { environment(it.key, it.value) }
        useJUnitPlatform {}
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    named<JavaExec>("run") {
        getEnvVariables().forEach { environment(it.key, it.value) }
        dependsOn(ktlintFormat)
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}

// PLUGINS
application {
    mainClass.set(argentMainClass)
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

jib {
    from.image = "gcr.io/distroless/java:11"
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
            "-Xms128m"
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

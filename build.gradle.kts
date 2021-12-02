plugins {
    kotlin("jvm") version "1.6.0"
    id("com.google.cloud.tools.jib") version "3.1.4"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    application
}

val ktorVersion = "1.6.5"
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
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")

    // Json
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("net.logstash.logback:logstash-logback-encoder:6.3")

    // Database
    implementation("com.google.cloud:google-cloud-firestore:3.0.7")

    // Secrets
    implementation("com.google.cloud:google-cloud-secretmanager:2.0.4")

    // JWT
    implementation("com.auth0:java-jwt:3.18.2")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
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
    val run by existing(JavaExec::class) {
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

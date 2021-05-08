plugins {
    kotlin("plugin.serialization") version "1.4.21"
    kotlin("jvm") version "1.4.21"
    id("com.google.cloud.tools.jib") version "2.7.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    application
}

val ktorVersion = "1.5.3"
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
    implementation(kotlin("stdlib-jdk8"))

    // Ktor
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")

    // Json
    implementation("com.fasterxml.jackson.core:jackson-core:2.9.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.7")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("net.logstash.logback:logstash-logback-encoder:6.3")

    // Database
    implementation("com.google.cloud:google-cloud-firestore:2.3.0")

    // Google
    implementation("com.google.cloud:google-cloud-secretmanager:1.4.0")
    implementation("com.google.cloud.sql:postgres-socket-factory:1.2.0")

    // JWT
    implementation("com.auth0:java-jwt:3.12.0")

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
        val envVars = getEnvVariables()
        this.systemProperties["io.ktor.development"] = envVars["ARGENT_DEBUG"] == "true"
        envVars.forEach { environment(it.key, it.value) }
        dependsOn(ktlintFormat)
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}

// PLUGINS
application {
    mainClass.set(argentMainClass)
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

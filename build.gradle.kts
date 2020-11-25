import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.serialization") version "1.4.10"
    kotlin("jvm") version "1.4.10"
    id("com.google.cloud.tools.jib") version "2.5.0"
    application
}

val ktorVersion = "1.4.1"
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
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")

    // Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("net.logstash.logback:logstash-logback-encoder:6.3")

    // Database
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("org.postgresql:postgresql:42.2.18")
    implementation("org.flywaydb:flyway-core:6.2.4")

    // Google
    implementation("com.google.cloud:google-cloud-secretmanager:1.0.0")
    implementation("com.google.cloud.sql:postgres-socket-factory:1.0.16")

    // JWT
    implementation("com.auth0:java-jwt:3.11.0")
    implementation("com.auth0:jwks-rsa:0.14.1")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
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
    kotlinOptions.jvmTarget = "11"
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

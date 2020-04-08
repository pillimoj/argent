import com.google.cloud.tools.jib.gradle.BuildImageTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "1.2.3"
val argentMainClass = "argent.MainKt"
java.sourceCompatibility = JavaVersion.VERSION_1_8

plugins {
    kotlin("jvm")
    id("com.google.cloud.tools.jib")
    application
}

val image = "argent"

group = "Argent"
version = "1.0-SNAPSHOT"

application {
    mainClassName = argentMainClass
}

sourceSets.main {
    withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
        kotlin.srcDirs("src")
    }
    resources {
        srcDir("resources")
    }
}

sourceSets.test {
    withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
        kotlin.srcDirs("test")
    }
    resources {
        srcDir("testresources")
        srcDir("../argent-client/build/distributions")
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
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")

    // Ktor client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:1.2.3")

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

    // Json
    implementation("com.fasterxml.jackson.core:jackson-core:2.9.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.7")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.0")

    // Google
    implementation("com.google.cloud:google-cloud-secretmanager:1.0.0")


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
            "-XX:InitialRAMPercentage=60.0",
            "-XX:MinRAMPercentage=60.0",
            "-XX:MaxRAMPercentage=60.0"
        )
    }
    to {
        image = image
    }
}

tasks.getByName<BuildImageTask>("jib") {
    dependsOn("test", ":argent-client:browserProductionWebpack")
}

tasks.getByName("jibDockerBuild") {
    dependsOn(":argent-client:browserProductionWebpack")
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

tasks.getByName<ProcessResources>("processResources"){
    from("../argent-client/build") {
        include("distributions/**")
    }
    mustRunAfter(":argent-client:browserProductionWebpack")
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

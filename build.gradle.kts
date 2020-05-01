import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val kotlinVersion = "1.3.71"
val serializationVersion = "0.20.0"
val ktorVersion = "1.3.2"

plugins {
    kotlin("multiplatform") version "1.3.71"
    application //to run JVM part
    kotlin("plugin.serialization") version "1.3.70"
}

group = "argent"
version = "1.0-SNAPSHOT"

repositories {
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/") // react, styled, ...
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    jvm {
        withJava()
    }
    js {
        browser {
            this.webpackTask {
                this.devServer
            }
            dceTask {
                keep("ktor-ktor-io.\$\$importsForInline\$\$.ktor-ktor-io.io.ktor.utils.io")
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("com.benasher44:uuid:0.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
                kotlinOptions.jvmTarget = "1.8"
            }
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-html-builder:$ktorVersion")

                implementation(kotlin("stdlib", kotlinVersion)) // or "stdlib-jdk8"
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion") // JVM dependency
                implementation("io.ktor:ktor-websockets:$ktorVersion")

                // Database
                val exposedVersion = "0.20.2"
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
                implementation("com.zaxxer:HikariCP:3.4.2")
                implementation("org.postgresql:postgresql:42.2.2")
                implementation("org.flywaydb:flyway-core:6.2.4")

                // Logging
                implementation("ch.qos.logback:logback-classic:1.2.3")
                implementation("net.logstash.logback:logstash-logback-encoder:6.3")

                // Google
                implementation("com.google.cloud:google-cloud-secretmanager:1.0.0")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
                implementation("io.ktor:ktor-server-test-host:$ktorVersion")
                implementation("com.github.javafaker:javafaker:1.0.2")
            }
            val test = tasks.withType<Test> {
                getEnvVariables().forEach { environment(it.key, it.value) }
                useJUnitPlatform {}
                testLogging {
                    events("passed", "skipped", "failed")
                }
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serializationVersion")
                //todo: bugfix in kx.serialization?
                implementation(npm("text-encoding"))
                implementation(npm("abort-controller"))

                implementation("io.ktor:ktor-client-js:$ktorVersion") //include http&websockets
                //todo: bugfix in ktor-client?
                implementation(npm("bufferutil")) //TODO: Uncomment this and stuff breaks. WHY?
                implementation(npm("utf-8-validate"))

                //ktor client js json
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-js:$ktorVersion")
                implementation(npm("fs"))

                implementation("org.jetbrains:kotlin-react:16.13.0-pre.94-kotlin-1.3.70")
                implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.94-kotlin-1.3.70")
                implementation("org.jetbrains:kotlin-react-router-dom:4.3.1-pre.94-kotlin-1.3.70")
                implementation(npm("react", "16.13.0"))
                implementation(npm("react-dom", "16.13.0"))
                implementation(npm("react-router-dom", "4.3.1"))

                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.94-kotlin-1.3.70")
                implementation(npm("styled-components", "4.4.0"))
                implementation(npm("inline-style-prefixer"))
            }
        }
    }
}

application {
    mainClassName = "argent.MainKt"
}

// include JS artifacts in any JAR we generate
tasks.getByName<ProcessResources>("jvmProcessResources") {
    val taskName = if (project.hasProperty("isProduction")) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(webpackTask.destinationDirectory!!){
        into("./public")
    }// bring output file along into the JAR
}

distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}


tasks.getByName<JavaExec>("run") {
    getEnvVariables().forEach { environment(it.key, it.value) }
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

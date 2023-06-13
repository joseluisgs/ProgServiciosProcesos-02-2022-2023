plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "dev.joseluisgs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}
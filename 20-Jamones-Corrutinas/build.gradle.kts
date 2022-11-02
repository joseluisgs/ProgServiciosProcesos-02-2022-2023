import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
    // para serializar Json y otros
    kotlin("plugin.serialization") version "1.7.20"
}

group = "es.joseluisgs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // Para hacer el logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")
    implementation("ch.qos.logback:logback-classic:1.4.4")
    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // Para serializar con Kotlin Serialization
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    // Serializa Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
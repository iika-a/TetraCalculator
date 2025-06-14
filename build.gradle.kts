plugins {
    kotlin("jvm") version "2.0.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.20.1")
    implementation("org.json:json:20250517")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
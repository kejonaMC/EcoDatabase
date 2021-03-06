plugins {
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1")
    compileOnly("org.jetbrains:annotations:22.0.0")
}

description = "common"
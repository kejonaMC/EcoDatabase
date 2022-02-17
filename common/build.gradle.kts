plugins {
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://libraries.minecraft.net/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1")
}

description = "common"
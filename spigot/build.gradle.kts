import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow")

}

repositories {
    maven("https://libraries.minecraft.net/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    api(project(":common"))
}


tasks.withType<ShadowJar> {
    dependencies {
        shadow {
            exclude("org.spigotmc:spigot-api")
            exclude("com.github.MilkBowl:VaultAPI")
        }
    }
    println(destinationDirectory.get())
    archiveFileName.set("EcoDatabase.jar")
    println(archiveFileName.get())
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "spigot"
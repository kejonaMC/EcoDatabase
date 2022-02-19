rootProject.name = "EcoDatabase"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("com.github.johnrengelman.shadow") version "7.1.0" // shadowing dependencies
    }
}

include(":common")
include(":spigot")
include(":sponge")
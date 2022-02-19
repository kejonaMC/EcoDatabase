import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    `java-library`
    id("org.spongepowered.gradle.plugin") version "2.0.1"
    id("com.github.johnrengelman.shadow")
    checkstyle
}

group = "dev.projectg"
version = "0.1.0"

repositories {
    mavenCentral()
}

sponge {
    apiVersion("8.0.0")
    license("MIT")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("ecodatabase") {
        displayName("EcoDatabase")
        entrypoint("dev.projectg.ecodatabase")
        description("eco database aync")
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "7.0.2"
    distributionType = Wrapper.DistributionType.BIN
}

val javaTarget = 8 // Sponge targets a minimum of Java 8
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
}

// Make sure all tasks which produce archives (jar, sources jar, javadoc jar, etc) produce more consistent output
tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

dependencies {
    testImplementation("org.spongepowered:spongeapi:8.0.0")
    api(project(":common"))
}

tasks.shadowJar {
    archiveBaseName.set("EcoDatabase")
    dependencies {
        include(dependency(":common"))
    }
}

tasks.jar {
    enabled = false
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.runServer {
    dependsOn(tasks.shadowJar)
}

tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}
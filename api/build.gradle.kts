import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    `java-library`
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
}

group = "com.astrainteractive.empire_items.api"
java {
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_17
}
repositories {
    mavenLocal()
    mavenCentral()
    maven(Dependencies.Repositories.clojars)
    maven(Dependencies.Repositories.playpro)
    maven(Dependencies.Repositories.dv8tion)
    maven(Dependencies.Repositories.maven2)
    maven(Dependencies.Repositories.enginehub)
    maven(Dependencies.Repositories.maven2Apache)
    maven(Dependencies.Repositories.dmulloy2)
    maven(Dependencies.Repositories.essentialsx)
    maven(Dependencies.Repositories.scarsz)
    maven(Dependencies.Repositories.papermc)
    maven(Dependencies.Repositories.spigotmc)
    maven(Dependencies.Repositories.extendedclip)
    maven(Dependencies.Repositories.jitpack)
    maven {
        url = uri(Dependencies.Repositories.lumine)
        metadataSources {
            artifact()
        }
    }
    flatDir { dirs("libs") }
}

dependencies {

    implementation(Dependencies.Implementation.kotlinGradlePlugin)
    implementation(Dependencies.Implementation.kotlinxCoroutines)
    implementation(Dependencies.Implementation.kotlinxCoroutinesCore)
    implementation(Dependencies.Implementation.kotlinxSerialization)
    implementation(Dependencies.Implementation.kotlinxSerializationJson)
    implementation(Dependencies.Implementation.kotlinxSerializationYaml)
    // AstraLibs
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.20")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:2.26.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.3.1")
    testImplementation("io.kotest:kotest-assertions-core:5.3.1")
    testImplementation(kotlin("test"))
    // Spigot dependencies
    compileOnly(Dependencies.CompileOnly.protocolLib)
    compileOnly(Dependencies.CompileOnly.essentialsX)
    compileOnly(Dependencies.CompileOnly.paperMC)
    compileOnly(Dependencies.CompileOnly.spigotApi)
    compileOnly(Dependencies.CompileOnly.spigot)
    compileOnly(Dependencies.CompileOnly.placeholderapi)
    compileOnly(Dependencies.CompileOnly.worldguard)
    compileOnly(Dependencies.CompileOnly.discordsrv)
    compileOnly(Dependencies.CompileOnly.vaultAPI)
    compileOnly(Dependencies.CompileOnly.coreprotect)
    compileOnly(Dependencies.CompileOnly.modelengine)
    implementation(kotlin("script-runtime"))
}
tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
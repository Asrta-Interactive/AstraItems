plugins {
    kotlin("plugin.serialization")
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("basic-plugin")
    id("basic-shadow")
    id("basic-resource-processor")
}

dependencies {
    // Kotlin
    implementation(libs.kotlinGradlePlugin)
    // Coroutines
    implementation(libs.coroutines.coreJvm)
    implementation(libs.coroutines.core)
    // Serialization
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.serializationJson)
    implementation(libs.kotlin.serializationKaml)
    // AstraLibs
    implementation(libs.astralibs.ktxCore)
    implementation(libs.astralibs.spigotCore)
    // Test
    testImplementation(kotlin("test"))
    implementation(libs.orgTesting)
    // Spigot dependencies
    implementation(libs.essentialsx)
    implementation(libs.paperApi)
    implementation(libs.spigotApi)
    implementation(libs.spigot)
    implementation(libs.protocollib)
    implementation(libs.placeholderapi)
    implementation(libs.worldguard.bukkit)
    implementation(libs.discordsrv)
    implementation(libs.vaultapi)
    implementation(libs.coreprotect)
    // Local
    implementation(project(":enchantements"))
    implementation(project(":modelengine"))
    implementation(project(":api"))
    implementation(project(":models"))
}

